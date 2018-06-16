package giraffe.com.location;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.giraffe.Users.MyFriendsActivity;

import java.util.ArrayList;
import java.util.List;

import giraffe.com.location.utils.Locationutils;
import giraffe.com.location.utils.PathSmoothTool;

public class MainActivity extends AppCompatActivity implements AMap.OnMapLoadedListener, LocationSource, AMapLocationListener, NavigationView.OnNavigationItemSelectedListener {

    //地图声明
    private MapView mMapView = null;
    private AMap aMap;

    //定位需要的声明
    private AMapLocationClient mlocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器
    private MyLocationStyle myLocationStyle;

    private LatLng oldLatLng = null;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ArrayList<LatLng> myLatLngs;
    private Polyline polyline;

    private PathSmoothTool mpathSmoothTool;

    private boolean isShow = false;
    private boolean isFirstLoc = true;
    private boolean clear = false;
    private boolean openTrack = false;
    private boolean isLoadData = false;

    double a;
    double b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locationutils.MAINCONTEXT = MainActivity.this.getBaseContext();

        init(savedInstanceState);

        mpathSmoothTool = new PathSmoothTool();
        mpathSmoothTool.setIntensity(4);

        if (aMap == null) {
            aMap = mMapView.getMap();

        }

        Intent intent = getIntent();
        a = intent.getDoubleExtra("lat", 0);
        b = intent.getDoubleExtra("long", 0);
        isShow = intent.getBooleanExtra("showFriend", false);
        location();
    }

    // 初始化参数
    private void init(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer1);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);//设置监听器
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_id);
        navigationView.setNavigationItemSelectedListener(this);

        mMapView = (MapView) findViewById(R.id.mapvw_id);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
    }

    //地图客户端控制
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void location() {
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);

        //设置定位的类型为定位模式,用Style来代替
        aMap.setMyLocationStyle(myLocationStyle);
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        //设置定位监听
        aMap.setLocationSource(this);
        //设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置默认定位按钮是否显示，非必需设置。
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setInterval(1000);
            mLocationOption.setNeedAddress(true);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }


    @Override
    public void onLocationChanged(AMapLocation location) {

        if (mListener != null && location != null) {
            if (location != null
                    && location.getErrorCode() == 0) {
                // 显示系统小蓝点
                mListener.onLocationChanged(location);
                if (isFirstLoc) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.
                            changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    isFirstLoc = false;
                }

                if (isShow) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.lo1)));
                    markerOptions.position(new LatLng(a, b));
                    aMap.addMarker(markerOptions);
                }

                //开启足迹
                if (openTrack) {
                    LatLng newLatLng = Locationutils.getLocationLatLng(location);
                    if (myLatLngs == null) {
                        myLatLngs = new ArrayList<>();
                    }
                    myLatLngs.add(newLatLng);
                    setUpMap(myLatLngs);
                } else {
                    myLatLngs = null;
                }

                //上传位置
                if (isLoadData) {
                    UpLoadTask upLoadTask = new UpLoadTask();
                    upLoadTask.execute(location.getLatitude(), location.getLongitude());
                    isLoadData = false;
                }

                if (clear) {
                    clear = false;
                    String locationStr = Locationutils.getLocationStr(location);
                    Intent intent = new Intent(MainActivity.this, BuffActivity.class);
                    intent.putExtra("buff", locationStr);
                    startActivity(intent);
                }
            }
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + location.getErrorCode() + ", errInfo:"
                    + location.getErrorInfo());
            Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
        }
    }

    private PolylineOptions options = new PolylineOptions();

    // 绘制足迹
    private void setUpMap(ArrayList<LatLng> myLatLngs) {
        if (myLatLngs != null && myLatLngs.size() > 0) {
            pathOptimize(myLatLngs);
        }
    }

    //轨迹平滑优化
    public List<LatLng> pathOptimize(List<LatLng> originlist) {
        List<LatLng> pathoptimizeList = mpathSmoothTool.pathOptimize(originlist);
        aMap.addPolyline(new PolylineOptions().addAll(pathoptimizeList).color(Color.BLUE));
        return pathoptimizeList;
    }

    //地图生命周期
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    //侧滑菜单控制
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.track:
                if (openTrack == false) {
                    openTrack = true;
                    item.setTitle("关闭足迹");
                    Toast.makeText(this, "开启足迹", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    openTrack = false;
                    item.setTitle("开启足迹");
                    Toast.makeText(this, "关闭足迹", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.friend:
                Intent intent = new Intent(MainActivity.this, MyFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.upload:
                isLoadData = true;
                break;
            case R.id.clearTrack:
                clear = true;
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //右上角设置菜单切换图层
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.map_type_normal:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.map_type_satellite:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_type_night:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //侧滑菜单的退出
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLoaded() {
        aMap.showBuildings(true);//隐藏3D楼块
    }
}