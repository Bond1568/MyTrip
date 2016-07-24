package com.hao.bond.mytrip.bean;

/**
 * Created by Bond on 2016/4/12.
 */
public class ItemBeanNavi {
    private String Dname,Daddress;

   public ItemBeanNavi(String dname,String daddress){
       Dname = dname;
       Daddress = daddress;
   }

    public String getDname() {
        return Dname;
    }

    public void setDname(String dname) {
        Dname = dname;
    }

    public String getDaddress() {
        return Daddress;
    }

    public void setDaddress(String daddress) {
        Daddress = daddress;
    }
}
