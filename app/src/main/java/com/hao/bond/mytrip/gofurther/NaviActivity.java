package com.hao.bond.mytrip.gofurther;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.hao.bond.mytrip.Adapter.BusResultListAdapter;
import com.hao.bond.mytrip.Adapter.NaviDestinatinAdapter;
import com.hao.bond.mytrip.R;
import com.hao.bond.mytrip.bean.ItemBeanNavi;
import com.hao.bond.mytrip.util.AMapUtil;
import com.hao.bond.mytrip.util.ConvertLatlon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Bond on 2016/4/7.
 */
public class NaviActivity extends Activity implements Inputtips.InputtipsListener, TextWatcher, RouteSearch.OnRouteSearchListener, GeocodeSearch.OnGeocodeSearchListener {
    private AutoCompleteTextView sptext, destext;
    private ImageView naviTopBack;
    private ImageView mode1, mode2, mode3, mode4;
    private TextView naviTopSearch;
    private ImageView locationGreen, locationYellow, loopimage;

    private GeocodeSearch geocodeSearch;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;  //latitude and longitude 经度和纬度
    private RouteSearch.FromAndTo mfromAndTo;
    private RouteSearch mRouteSearch;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private int routeType = 1;
    private double mlatitude;
    private double mlongitude;
    private RouteSearch.FromAndTo fromAndTo;

    private ListView searchListView;
    private List<ItemBeanNavi> naviDataList;
    private ItemBeanNavi itembeannavi;
    private List<Map<String, String>> inputTipsList;
    private NaviDestinatinAdapter naviDestinationAdapter;

    private String locationAddress;
    private String citycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_activity);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);//这里产生漏洞啊，忘了初始化和设置监听了
        initViews();
        getIntentData();
    }

    /**
     * get location information by intent from FragmentGo
     */
    private void getIntentData() {
        Intent intent = this.getIntent();
        if (intent.getStringExtra("locationAddress") != null) {
            locationAddress = intent.getStringExtra("locationAddress");
            citycode = intent.getStringExtra("citycode");
            mlatitude = Double.parseDouble(intent.getStringExtra("loclatitude"));
            mlongitude = Double.parseDouble(intent.getStringExtra("loclongitude"));

        }
    }

    /**
     * 初始化layout中的view
     */
    private void initViews() {
        naviTopBack = (ImageView) findViewById(R.id.navi_top_back);
        naviTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NaviActivity.this.finish();
            }
        });
        mode1 = (ImageView) findViewById(R.id.navi_top_mode1);
        mode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeType = 1;
                resetTopColor();
                mode1.setImageResource(R.drawable.busnormal);
            }
        });
        mode2 = (ImageView) findViewById(R.id.navi_top_mode2);
        mode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeType = 3;
                resetTopColor();
                mode2.setImageResource(R.drawable.walknormal);
            }
        });
        mode3 = (ImageView) findViewById(R.id.navi_top_mode3);
        mode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeType = 3;
                resetTopColor();
                mode3.setImageResource(R.drawable.bicyclenormal);
            }
        });
        mode4 = (ImageView) findViewById(R.id.navi_top_mode4);
        mode4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTopColor();
                mode4.setImageResource(R.drawable.mixnormal);
            }
        });
        locationGreen = (ImageView) findViewById(R.id.imagegreen);
        locationGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sptext.setText(locationAddress);
            }
        });
        locationYellow = (ImageView) findViewById(R.id.imageyellow);
        locationYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destext.setText(locationAddress);
            }
        });
        sptext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        sptext.addTextChangedListener(this);
        sptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sptext.getText().toString().trim().equals("我的位置")) {
                    sptext.setText("");
                }

            }
        });
        destext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        destext.addTextChangedListener(this);
        destext.requestFocus();
        loopimage = (ImageView) findViewById(R.id.loop);
        loopimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String middle = sptext.getText().toString();
                sptext.setText(destext.getText().toString());
                destext.setText(middle);
            }
        });
        naviTopSearch = (TextView) findViewById(R.id.navi_top_search);
        naviTopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (routeType == 1) {
//                    searchRouteResult(1, RouteSearch.BusDefault,citycode);
//                }
//                if (routeType == 2) {
//                    searchRouteResult(3,RouteSearch.WalkDefault,citycode);
//                }
//                if (routeType == 3) {
//                    searchRouteResult(3,RouteSearch.WalkDefault,citycode);
//                }

                startLatlonPoint(sptext.getText().toString());
                if (sptext.getText().length() != 0 &&destext.getText().length()!=0) {
//                    Toast.makeText(NaviActivity.this,"起点经纬度："+mStartPoint.getLatitude()+"**"+mStartPoint.getLongitude()+"\n"+"终点经纬度："+mEndPoint.getLatitude()+"**"+mEndPoint.getLongitude(),Toast.LENGTH_SHORT).show();
                    mfromAndTo = new RouteSearch.FromAndTo(mStartPoint,mEndPoint);
                    if (routeType == 1) {
                        searchRouteResult(1, RouteSearch.BusDefault, citycode, mfromAndTo);

                        searchListView.setVisibility(View.GONE);
                    }

                    if (routeType == 3) {
                    searchRouteResult(3,RouteSearch.WalkDefault,citycode,mfromAndTo);
                }
//                    naviDataList = new ArrayList<ItemBeanNavi>();
//                    addDataSet(naviDataList);
//                    naviDestinationAdapter = new NaviDestinatinAdapter(NaviActivity.this, naviDataList);
//                    searchListView.setAdapter(naviDestinationAdapter);
//                    searchListView.setVisibility(View.VISIBLE);
                }

            }
        });
        searchListView = (ListView) findViewById(R.id.search_listView);
    }

    private void startLatlonPoint(String addressString) {

        geocodeSearch = new GeocodeSearch(NaviActivity.this);
        if (addressString.equals("我的位置")) {
            mStartPoint = new LatLonPoint(mlatitude, mlongitude);

        }
        if (addressString != "我的位置" && addressString.length() > 0) {
        }
        if (addressString.length() == 0) Toast.makeText(this, "请设置起点", Toast.LENGTH_SHORT).show();

    }

    public void requestLatlonPoint(String address,String code) {
        geocodeSearch = new GeocodeSearch(NaviActivity.this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        GeocodeQuery query = new GeocodeQuery(address,code);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    private void addDataSet(List<ItemBeanNavi> naviDataList) {
        for (int i = 0; i < 20; i++) {
            itembeannavi = new ItemBeanNavi("目的地" + i, "详细地址" + i);
            naviDataList.add(itembeannavi);
        }
    }

    /**
     * 发送输入提示后得到回调方法
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            inputTipsList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", tipList.get(i).getName());
                map.put("district", tipList.get(i).getDistrict());
                inputTipsList.add(map);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, inputTipsList, R.layout.item_destination_list, new String[]{"name", "district"}, new int[]{R.id.search_Dname, R.id.search_Daddress});
            searchListView.setAdapter(simpleAdapter);
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (destext.hasFocus()) {     //因为分不清到底是哪个autocompletetextview的文本发生了改变，所以用光标来加以判断
                        destext.setText(inputTipsList.get(i).get("name"));
                        requestLatlonPoint(inputTipsList.get(i).get("name"),citycode);
                    }
                    if (sptext.hasFocus()) {
                        sptext.setText(inputTipsList.get(i).get("name"));
                        requestLatlonPoint(inputTipsList.get(i).get("name"),citycode);
                    }

                }
            });

//            simpleAdapter.notifyDataSetChanged();
        } else {
//            ToastUtil.showerror(this, rCode);
            Toast.makeText(this, "亲，貌似没联网哟", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /**
     * 监听到文本输入框改动时，请求一个新的输入提示
     */
    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        String newText = s.toString().trim();
        if ((newText != null) && (newText.trim().length() != 0)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, citycode);
            Inputtips inputTips = new Inputtips(NaviActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();


            searchListView.setVisibility(View.VISIBLE);
        } else {
            searchListView.setVisibility(View.GONE);

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void resetTopColor() {
        mode1.setImageResource(R.drawable.buspress);
        mode2.setImageResource(R.drawable.walkpress);
        mode3.setImageResource(R.drawable.bicyclepress);
        mode4.setImageResource(R.drawable.mixpress);
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode, String mCurrentCityCode,RouteSearch.FromAndTo fromAndTo) {
        if (mStartPoint == null) {
//            ToastUtil.show(mContext, "定位中，稍后再试...");
            Toast.makeText(this, "定位中，稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEndPoint == null) {
//            ToastUtil.show(mContext, "终点未设置");
            Toast.makeText(this, "终点未设置", Toast.LENGTH_SHORT).show();
        }
//        showProgressDialog();

//        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                mStartPoint, mEndPoint);
        if (routeType == 1) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    mCurrentCityCode, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        }
//        else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
//            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
//                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
//            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
//        }
         if (routeType == 3) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    /**
     * get bus route result
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
//        dissmissProgressDialog();
        searchListView.setVisibility(View.GONE);
//        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
//                    Toast.makeText(NaviActivity.this,"起点经纬度："+mStartPoint.getLatitude()+"**"+mStartPoint.getLongitude()+"\n"+"终点经纬度："+mEndPoint.getLatitude()+"**"+mEndPoint.getLongitude(),Toast.LENGTH_SHORT).show();
                    mBusRouteResult = result;
                    fromAndTo = new RouteSearch.FromAndTo(mStartPoint,mEndPoint);
                    BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(NaviActivity.this, mBusRouteResult,fromAndTo);
                    searchListView.setAdapter(mBusResultListAdapter);
                    searchListView.setVisibility(View.VISIBLE);
                } else if (result != null && result.getPaths() == null) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "busRouteSearchError", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "busRouteSearchErrorCode", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        searchListView.setVisibility(View.GONE);
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    fromAndTo = new RouteSearch.FromAndTo(mStartPoint,mEndPoint);
                    Intent intent = new Intent(this,BusRouteDetailActivity.class);
                    intent.putExtra("start_end",fromAndTo);
                    intent.putExtra("walk_result",mWalkRouteResult);
                    startActivity(intent);
                }
            }
        }
    }
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }




    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                if (sptext.hasFocus()){
                    mStartPoint = address.getLatLonPoint();
                }if (destext.hasFocus()){
                    mEndPoint = address.getLatLonPoint();
                }

//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
//                geoMarker.setPosition(AMapUtil.convertToLatLng(address
//                        .getLatLonPoint()));
//                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                        + address.getFormatAddress();
            }
        }
    }
}
