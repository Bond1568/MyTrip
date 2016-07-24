package com.hao.bond.mytrip.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.hao.bond.mytrip.R;
import com.hao.bond.mytrip.gofurther.BusRouteDetailActivity;
import com.hao.bond.mytrip.util.AMapUtil;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Bond on 2016/4/15.
 */
public class BusResultListAdapter extends BaseAdapter {
    private Context mContext;
    private BusRouteResult mBusRouteResult;
    private List<BusPath> mBusPathList;
    RouteSearch.FromAndTo startToend;

    public BusResultListAdapter(Context context, BusRouteResult busRouteResult, RouteSearch.FromAndTo fromAndTo) {
        mContext = context;
        mBusPathList = busRouteResult.getPaths();
        mBusRouteResult = busRouteResult;
        startToend = fromAndTo;
    }

    @Override
    public int getCount() {
        return mBusPathList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBusPathList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder1 holder1 = null;
        if (view == null) {
            holder1 = new ViewHolder1();
            view = View.inflate(mContext, R.layout.item_bus_result, null);
            holder1.title = ((TextView) view.findViewById(R.id.title));
            holder1.des = ((TextView) view.findViewById(R.id.describe));
            view.setTag(holder1);
        } else {
            holder1 = (ViewHolder1) view.getTag();
        }

        final BusPath item = mBusPathList.get(i);
        holder1.title.setText(AMapUtil.getBusPathTitle(item));
        holder1.des.setText(AMapUtil.getBusPathDes(item));

       view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(),
                        BusRouteDetailActivity.class);
                intent.putExtra("bus_path", item);
                intent.putExtra("bus_result", mBusRouteResult);
                intent.putExtra("start_end",startToend);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

        return view;
    }

    private class ViewHolder1 {
        TextView title;
        TextView des;


    }
}
