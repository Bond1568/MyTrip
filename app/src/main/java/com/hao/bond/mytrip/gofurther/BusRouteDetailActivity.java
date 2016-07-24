package com.hao.bond.mytrip.gofurther;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.aps.amapapi.model.AmapLoc;
import com.hao.bond.mytrip.R;
import com.hao.bond.mytrip.util.AMapUtil;

/**
 * Created by Bond on 2016/4/19.
 */
public class BusRouteDetailActivity extends Activity  implements AMap.OnMarkerClickListener {
    private TextView routeDetail, arNavi;
    private ImageView showDetailList;

    private AMap aMap;
    private MapView mapView;
    private BusPath busPath;
    private WalkPath walkPath;
    private BusRouteResult busRouteResult;
    private WalkRouteResult walkRouteResult;
    private RouteSearch.FromAndTo fromAndTo;
    private RelativeLayout bottomlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_route_detail_activity);
        getIntentData();
        initViews();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        aMap.setOnMarkerClickListener(BusRouteDetailActivity.this);

        changeCamera(  //将两点的中点移到屏幕中央
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng((fromAndTo.getFrom().getLatitude() + fromAndTo.getTo().getLatitude()) / 2, (fromAndTo.getFrom().getLongitude() + fromAndTo.getTo().getLongitude()) / 2), 11, 0, 0)), this);
//        LatLngBounds var1 = this.getLatLngBounds();
//        this.aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(var1, 50));
        setfromandtoMarker();
        showOverlayOnMap();

    }
//    protected LatLngBounds getLatLngBounds() {
//        LatLngBounds.Builder var1 = LatLngBounds.builder();
////        var1.include(new LatLng(this.fromAndTo.getFrom().getLatitude(), this.fromAndTo.getFrom().getLongitude()));
////        var1.include(new LatLng(this.fromAndTo.getTo().getLatitude(), this.fromAndTo.getTo().getLongitude()));
//        var1.include(new LatLng(this.busRouteResult.getStartPos().getLatitude(), this.busRouteResult.getStartPos().getLongitude()));
//        var1.include(new LatLng(this.busRouteResult.getTargetPos().getLatitude(), this.busRouteResult.getTargetPos().getLongitude()));
//        return var1.build();
//    }
    private void showOverlayOnMap() {
        aMap.clear();
        if (busRouteResult != null) {
            routeDetail.setText(AMapUtil.getBusPathDes(busPath));
            BusRouteOverlay busRouteOverlay = new BusRouteOverlay(this, aMap, busRouteResult.getPaths().get(0), busRouteResult.getStartPos(), busRouteResult.getTargetPos());
            busRouteOverlay.removeFromMap();
            busRouteOverlay.addToMap();
            busRouteOverlay.zoomToSpan();

        }
        if (walkRouteResult != null) {
//            routeDetail.setText(walkRouteResult.getPaths().);
            WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,aMap,walkRouteResult.getPaths().get(0),walkRouteResult.getStartPos(),walkRouteResult.getTargetPos());
            walkRouteOverlay.removeFromMap();
            walkRouteOverlay.addToMap();
            walkRouteOverlay.zoomToSpan();
        }

    }

    private void changeCamera(CameraUpdate cameraUpdate, BusRouteDetailActivity busRouteDetailActivity) {
        aMap.moveCamera(cameraUpdate);
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    private void initViews() {
        bottomlayout = (RelativeLayout) findViewById(R.id.bottom);
        routeDetail = (TextView) findViewById(R.id.textView1);

        arNavi = (TextView) findViewById(R.id.textView2);
        showDetailList = (ImageView) findViewById(R.id.imageView1);
        showDetailList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusRouteDetailActivity.this,BusRouteDetailFurtherActivitity.class);
                startActivity(intent);

            }
        });
    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(fromAndTo.getFrom()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(fromAndTo.getTo()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }

    private void getIntentData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            walkRouteResult = intent.getParcelableExtra("walk_result");
            busPath = intent.getParcelableExtra("bus_path");
            busRouteResult = intent.getParcelableExtra("bus_result");
            fromAndTo = intent.getParcelableExtra("start_end");
        }
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
