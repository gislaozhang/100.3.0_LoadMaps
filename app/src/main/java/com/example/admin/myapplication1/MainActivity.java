package com.example.admin.myapplication1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;

import java.io.File;
import java.util.List;

/**
 * 加载spk
 */
public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private SceneView mSceneView;
    private ArcGISScene scene;
    private MobileMapPackage mapPackage;

    private ArcGISMap mMap = null;
    Layer layer = null;
    private final String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMMPKFromSdcard();
        //getTpkFromSdcard();
        //getTileFromUrl();
    }

    /**
     * 获取在线切片服务
     */
    private void getTileFromUrl() {
        mMapView = (MapView) findViewById(R.id.mapView);
        String tileurl = "http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
        ArcGISTiledLayer tileLayer = new ArcGISTiledLayer(tileurl);
        Basemap basemap = new Basemap(tileLayer);
        mMap = new ArcGISMap(basemap);
        Envelope mInitExtent = new Envelope(12538169.930, 2425772.693, 13677998.896, 4235801.523, SpatialReference.create(102100));
        Viewpoint vp = new Viewpoint(mInitExtent);
        mMap.setInitialViewpoint(vp);
        mMapView.setMap(mMap);
    }

    /**
     * 从sdcard根目录获得移动地图包
     */
    private void getMMPKFromSdcard() {
        mMapView = (MapView) findViewById(R.id.mapView);

        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            String sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();// 获取sdcard的根路径
            Log.d("path", sdpath.toString());

            //获取tpk文件在手机sdcard中的存放路径
            String filepath = sdpath + File.separator + "chinatest.mmpk";
            if (!filepath.isEmpty()) {
                // create the mobile map package
                mapPackage = new MobileMapPackage(filepath);
                // load the mobile map package asynchronously
                mapPackage.loadAsync();
                // add done listener which will invoke when mobile map package has loaded
                mapPackage.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        // check load status and that the mobile map package has maps
                        if (mapPackage.getLoadStatus() == LoadStatus.LOADED && mapPackage.getMaps().size() > 0) {
                            mMap = mapPackage.getMaps().get(0);
                            Envelope mInitExtent = new Envelope(12538169.930, 2425772.693, 13677998.896, 4235801.523, SpatialReference.create(102100));

                            Viewpoint vp = new Viewpoint(mInitExtent);

                            mMap.setInitialViewpoint(vp);
                            mMapView.setMap(mMap);

                            mMapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
                                @Override
                                public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                                    if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
//                                        progressBar.setVisibility(View.VISIBLE);
                                        Log.d("drawStatusChanged", "spinner visible");
                                    } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
//                                        progressBar.setVisibility(View.INVISIBLE);

                                        List<ArcGISMap> mainArcGISMapL = mapPackage.getMaps();
                                        mMap = mainArcGISMapL.get(0);
                                        int size = mMap.getOperationalLayers().size();
                                        Log.d("output ", "getFullExtent:" + mMap.getOperationalLayers().size());

                                        for (int i = 0; i < size; i++) {
                                            Log.d("output ", "getFullExtent:" + mMap.getOperationalLayers().get(i).getName());
                                            Log.d("output ", "getFullExtent:" + mMap.getOperationalLayers().get(i).getFullExtent());

                                        }
                                    }
                                }
                            });


                        } else {
                            // Log an issue if the mobile map package fails to load
                            Log.e("MMPK", mapPackage.getLoadError().getMessage());
                        }
                    }
                });

            }
        } else {
            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 从sdcard根目录获得slpk文件
     */
//    private void getSlpkFromSdcard() {
//        // create a scene and add a basemap to it
//        ArcGISScene scene = new ArcGISScene();
////        scene.setBasemap(Basemap.createImagery());
//
//        mSceneView = (SceneView) findViewById(R.id.sceneView);
//        mSceneView.setScene(scene);
//
//        boolean isSdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
//        if (isSdCardExist) {
//            String sdpath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath();// 获取sdcard的根路径
//            Log.d("path", sdpath.toString());
//
//            //获取tpk文件在手机sdcard中的存放路径
//            String filepath = sdpath + File.separator + "Buildings.slpk";
//            if (!filepath.isEmpty()) {
//
//                // add a scene service to the scene for viewing buildings
//                ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(filepath);
//                scene.getOperationalLayers().add(sceneLayer);
//
////                // add a camera and initial camera position
//                Camera camera = new Camera(39.915559, 116.403811, 50, 345, 65, 0);
//                mSceneView.setViewpointCamera(camera);
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
//        }
//    }


    /**
     * 从sdcard根目录获得离线缓存数据
     */
//    private void getTileFromSdcard() {
//        boolean isSdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
//        if (isSdCardExist) {
//            String sdpath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath();// 获取sdcard的根路径
//            Log.d("path", sdpath.toString());
//            //获取离线缓存切片文件在手机sdcard中的存放路径
//            String filepath = sdpath + File.separator + "Layers";
//            Log.d("path", "moxinglujing" + filepath.toString());
//            if (!filepath.isEmpty()) {
//                TileCache tileCache = new TileCache(filepath);
//                ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
//                Basemap basemap = new Basemap(tiledLayer);
//                mMapView = (MapView) findViewById(R.id.mapView);
//                ArcGISMap map = new ArcGISMap(basemap);
//                mMapView.setMap(map);
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * 从sdcard根目录获得tpk数据
     */
    private void getTpkFromSdcard() {
        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            String sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();// 获取sdcard的根路径
            Log.d("path", sdpath.toString());

            //获取tpk文件在手机sdcard中的存放路径
            String filepath = sdpath + File.separator + "beijing.tpk";
            Log.d("path", "moxinglujing" + filepath.toString());
            if (!filepath.isEmpty()) {
                TileCache tileCache = new TileCache(filepath);
                ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
                Basemap basemap = new Basemap(tiledLayer);
                mMapView = (MapView) findViewById(R.id.mapView);
                ArcGISMap map = new ArcGISMap(basemap);
                mMapView.setMap(map);

                Log.d("output ", "getFullExtent:" + tiledLayer.getFullExtent());
            }
        } else {
            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从sdcard根目录获得tpk数据
     */
//    private void getSPKFromSdcard() {
//        boolean isSdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
//        if (isSdCardExist) {
//            String sdpath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath();// 获取sdcard的根路径
//            Log.d("path", sdpath.toString());
//
//            //获取tpk文件在手机sdcard中的存放路径
//            String filepath = sdpath + File.separator + "Buildings.slpk";
//            if (!filepath.isEmpty()) {
//
//                // add a scene service to the scene for viewing buildings
//                ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(filepath);
//                scene.getOperationalLayers().add(sceneLayer);
//
////                // add a camera and initial camera position
//                Camera camera = new Camera(48.378, -4.494, 200, 345, 65, 0);
////                mSceneView.setViewpointCamera(camera);
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
//        }
//    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
//        mSceneView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
//        mSceneView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
//        mSceneView.dispose();
    }
}