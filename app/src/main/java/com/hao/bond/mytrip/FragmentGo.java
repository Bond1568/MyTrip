package com.hao.bond.mytrip;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.hao.bond.mytrip.gofurther.NaviActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Bond on 2016/4/4.
 */
public class FragmentGo extends Fragment implements LocationSource,AMapLocationListener{
    private MapView mapView;
    private AMap aMap;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private HashMap<String,Object> greenLocationinfo ;
    private double loclatitude;
    private double loclongitude;

    private TextView textViewCity;
    private ImageView imageViewgo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_go,container,false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initViews();

        textViewCity = (TextView) view.findViewById(R.id.citylist);
        imageViewgo = (ImageView) view.findViewById(R.id.go_imageView);

        greenLocationinfo = new HashMap<String, Object>();//千万不能不初始化



        imageViewgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NaviActivity.class);

                if ( !greenLocationinfo.isEmpty() ) {
                    String citycode = greenLocationinfo.get("citycode")+"";
                    String locationAddress = greenLocationinfo.get("locationAddress")+"";
                    intent.putExtra("citycode", citycode);
                    intent.putExtra("locationAddress", locationAddress);
                    intent.putExtra("loclatitude", loclatitude+"");
                    intent.putExtra("loclongitude",loclongitude+"");

                }
                startActivity(intent);
            }
        });
        return view;
    }
    private Map<String,Object> greenCatchLocationData(AMapLocation amapLocation){
//        greenLocationinfo = new HashMap<String, String>();
        String cityname= amapLocation.getCity();
        String locationAddress =amapLocation.getAddress();
        String citycode = amapLocation.getCityCode();
        loclatitude = amapLocation.getLatitude();
        loclongitude = amapLocation.getLongitude();
        greenLocationinfo.put("cityname", cityname);
        greenLocationinfo.put("locationAddress", locationAddress);
        greenLocationinfo.put("citycode",citycode);
        textViewCity.setText(cityname);
        return greenLocationinfo;
    }

    private void initViews() {

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
//                mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                greenCatchLocationData(amapLocation);
            } else {
                String errText = "定位失败" + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
//                Toast.makeText(getActivity(),errText,Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
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


}
