package com.yscoco.blue.utils;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：karl.wei
 * 创建日期： 2018/5/22 0022
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：电压工具类
 */

public class VoltUtils {
    public static Map<Integer,VoltBean> voltMap = new HashMap<>();
    public static void clear(){
        voltMap.clear();
    }
    public static String getVolt(){
        float volt = 0;
        if(voltMap!=null){
            for (Integer key : voltMap.keySet()) {
                VoltBean bean = voltMap.get(key);
                volt += bean.getCurretVolt();
            }
        }
        DecimalFormat fnum  =   new  DecimalFormat("##0.0");
        return fnum.format(volt);
    }
    public static List<VoltBean> getVoltList(){
        List<VoltBean> voltList = new ArrayList<>();
        if(voltMap!=null){
            for (Integer key : voltMap.keySet()) {
                VoltBean bean = voltMap.get(key);
                int position = -1;
                boolean isContinue = true;
                for(int i=0;i<voltList.size()&isContinue;i++){
                    if(key<voltList.get(i).getPosition()){
                        position = i;
                        isContinue = false;
                    }
                }
                Log.e("blue",key+"::"+position);
                if(position==-1) {
                    voltList.add(bean);
                }else{
                    if(position<voltList.size()){
                        voltList.add(position, bean);
                    }else{
                        voltList.add(bean);
                    }
                }
            }
        }
        voltList.add(0,new VoltBean());
        return voltList;
    }
}
