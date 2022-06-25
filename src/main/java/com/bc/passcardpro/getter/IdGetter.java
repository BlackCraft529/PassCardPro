package com.bc.passcardpro.getter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 20:30
 */
public class IdGetter {

    public static String getWeekId(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return df.format(cal.getTime());
    }
}
