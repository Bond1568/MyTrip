package com.hao.bond.mytrip.util;


import android.content.Context;


import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;


/**
 * Created by Bond on 2016/4/16.****************此类返回latlonpoint的值总是null所以暂时不用了
 */
public class ConvertLatlon  implements GeocodeSearch.OnGeocodeSearchListener {
    private GeocodeSearch geocodeSearch;
    public  static LatLonPoint latLonPoint;

    public LatLonPoint getLatLonPoint() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+latLonPoint);

            return latLonPoint;
    }

    public  static void setLatLonPoint(LatLonPoint latLonPoint) {
        ConvertLatlon.latLonPoint = latLonPoint;
    }

    public ConvertLatlon(Context context,String startpoint,String code){
        geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(this);
        GeocodeQuery query = new GeocodeQuery(startpoint,code);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求

    }
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                ConvertLatlon.setLatLonPoint(address.getLatLonPoint());
                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+latLonPoint);
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
//                geoMarker.setPosition(AMapUtil.convertToLatLng(address
//                        .getLatLonPoint()));
//                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                        + address.getFormatAddress();
            }
        }
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

    }

}
