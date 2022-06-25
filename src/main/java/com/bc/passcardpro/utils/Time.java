package com.bc.passcardpro.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 15:11
 */
public class Time {
    /**
     * 计算和当日的天数差距
     * @param oldTime 当前日期
     * @param newTime 之前日期
     * @return 相差天数
     */
    public static int daysBetween(String oldTime, String newTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(oldTime));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(newTime));
            long time2 = cal.getTimeInMillis();
            long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(betweenDays));
        }catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * 计算和现在的分钟差距
     * @param oldTime 以前时间
     * @param newTime 现在时间
     * @return 分钟数
     * @throws ParseException 转换错误
     */
    public static double minuteBetween(String oldTime,String newTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nTime =df.parse(newTime).getTime();
        long oTime = df.parse(oldTime).getTime();
        long diff=(nTime-oTime)/1000/60;
        if(diff<1){
            return 0;
        }
        DecimalFormat dfs = new DecimalFormat("#.00");
        return Double.parseDouble(dfs.format(diff));
    }
}
