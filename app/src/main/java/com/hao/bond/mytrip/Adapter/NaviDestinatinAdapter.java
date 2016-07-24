package com.hao.bond.mytrip.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hao.bond.mytrip.R;
import com.hao.bond.mytrip.bean.ItemBeanNavi;

import java.util.List;

/**
 * Created by Bond on 2016/4/12.
 */
public class NaviDestinatinAdapter extends BaseAdapter {
    private List<ItemBeanNavi> beanNaviList;
    private LayoutInflater layoutInflater;
    public NaviDestinatinAdapter(Context context,List<ItemBeanNavi> list){
        beanNaviList = list;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return beanNaviList.size();
    }

    @Override
    public Object getItem(int i) {
        return beanNaviList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_destination_list,null);
            viewHolder = new ViewHolder();
            viewHolder.setTextView1((TextView) view.findViewById(R.id.search_Dname));
            viewHolder.setTextView2((TextView) view.findViewById(R.id.search_Daddress));
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.getTextView1().setText(beanNaviList.get(i).getDname());
            viewHolder.getTextView2().setText(beanNaviList.get(i).getDaddress());


        return view;
    }
}
class ViewHolder{
        private TextView textView1;
    private TextView textView2;

    public TextView getTextView1() {
        return textView1;
    }

    public void setTextView1(TextView textView1) {
        this.textView1 = textView1;
    }

    public TextView getTextView2() {
        return textView2;
    }

    public void setTextView2(TextView textView2) {
        this.textView2 = textView2;
    }
}