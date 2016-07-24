package com.hao.bond.mytrip;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    private FragmentManager fragmentManager;
    private FragmentGo fragmentGo;
    private FragmentFind fragmentFind;
    private FragmentMine fragmentMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager = getFragmentManager();
        setTabFragment(1);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
       switch (i){
           case R.id.main_tab_go:
               setColorTab1();
               setTabFragment(1);
               break;
           case R.id.main_tab_find:
               setColorTab2();
               setTabFragment(2);
               break;
           case R.id.main_tab_mine:
               setColorTab3();
               setTabFragment(3);
               break;
           default:
               break;
       }
    }

    /**
     * 使用此方法好处：
     *         1、通过调用oncreat中的setTabFragment(0)的方法减少代码的复用
     *         2、将监听事件中的方法引入进来，更加清晰
     * @param i
     */
    private void setTabFragment(int i){
        FragmentTransaction  transaction = fragmentManager.beginTransaction();//切换fragment关键在于每次切换都要新建一个FragmentTransaction的实例，而不必新建多个FragmentManager
        hideFragments(transaction);
        switch (i){
            case 1:
                if (fragmentGo == null) {
                    fragmentGo = new FragmentGo();
                    transaction.add(R.id.frame_fragment,fragmentGo);
                }else transaction.show(fragmentGo);
                break;
            case 2:
                if (fragmentFind == null) {
                    fragmentFind = new FragmentFind();
                    transaction.add(R.id.frame_fragment,fragmentFind);
                }
                else transaction.show(fragmentFind);
                break;
            case 3:
                if (fragmentMine == null) {
                    fragmentMine = new FragmentMine();
                    transaction.add(R.id.frame_fragment,fragmentMine);
                }else transaction.show(fragmentMine);
                break;
            default:
                break;
                }
                transaction.commit();
        }

    /**
     * 判断Fragment实例是否为空有必要，因为貌似空的实例不能被隐藏，会出错
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction){
        if (fragmentGo != null) {
            transaction.hide(fragmentGo);
        }
        if (fragmentFind != null) {
            transaction.hide(fragmentFind);
        }
        if (fragmentMine != null) {
            transaction.hide(fragmentMine);
        }
    }

    private void initViews(){
        radioGroup = (RadioGroup) findViewById(R.id.main_tab_radiogroup);
        radioButton1 = (RadioButton) findViewById(R.id.main_tab_go);
        radioButton2 = (RadioButton) findViewById(R.id.main_tab_find);
        radioButton3 = (RadioButton) findViewById(R.id.main_tab_mine);
        radioGroup.setOnCheckedChangeListener(this);
    }

    private void highlight(RadioButton radioButton){
        radioButton.setTextColor(Color.parseColor("#8ac745"));
    }
    private void nolight(RadioButton radioButton){
        radioButton.setTextColor(Color.parseColor("#9C9C9C"));
    }
    private void setColorTab1(){
        highlight(radioButton1);
        nolight(radioButton2);
        nolight(radioButton3);
    }
    private void setColorTab2() {
        highlight(radioButton2);
        nolight(radioButton1);
        nolight(radioButton3);
    }
    private void setColorTab3() {
        highlight(radioButton3);
        nolight(radioButton1);
        nolight(radioButton2);
    }
}
