package giraffe.com.location;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

import giraffe.com.location.R;

public class Testa extends Activity implements AMap.OnMapLoadedListener {
    private AMap aMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutaaa);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMapLoadedListener(this);
        aMap.showBuildings(true);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        aMap.showBuildings(true);//隐藏3D楼块
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.92463, 116.389139), 18, 60, 0)));
    }
}
