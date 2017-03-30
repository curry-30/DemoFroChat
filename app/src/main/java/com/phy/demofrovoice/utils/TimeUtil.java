package com.phy.demofrovoice.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by phy on 2016/11/22.
 * 时间操作及转换工具类
 */

public class TimeUtil {

    public static String getGTM() {
        return System.currentTimeMillis() / 1000 + "";
    }

    public static String getZoneGtm(){
        return System.currentTimeMillis()/1000 +"";
    }


    //格林威治标准时间
    public static long getGMTUnixTimeByCalendar() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getDate() {
        DateFormat format1 = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return format1.format(new Date());
    }

    /**
     * date格式转unixTime
     * @param date
     */
    public static long getTimeStamp(String date) {
        DateFormat formate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date timeStamp = null;
        try {
            timeStamp = formate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(timeStamp == null){
            return getGMTUnixTimeByCalendar();
        }
        return timeStamp.getTime()/1000;
    }

    //获取当天0点unixTime
    public static long getStartTime(){
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime()/1000;
    }

    /**
     * 比较时间大小
     *
     * @param DATE1
     * @param DATE2
     * @return 后面时间大则 返回true 否则返回false
     */
    public static Boolean isBeforBig(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return false;
            } else if (dt1.getTime() < dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return true;
        }
    }


    /**比较时间差距大
     * @param DATE1
     * @param DATE2
     * @return true -差1小时 false  不到一小时
     */
    public static Boolean isBeforTime(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            long diff;
            if (dt1.getTime() > dt2.getTime()) {
                diff = dt1.getTime() - dt2.getTime();
            } else if (dt1.getTime() < dt2.getTime()) {
                diff = dt2.getTime() - dt1.getTime();
            } else {
                diff = 0;
            }
            if(diff > 1000*60){
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return false;
    }

    /**unix时间转date时间
     * @param unixTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String unix2Date(String unixTime){
        if(isValidDate(unixTime)){
            unixTime = getTimeStamp(unixTime)+"";
        }
        Long timestamp = Long.parseLong(unixTime)*1000;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        return date;
    }

    /**获取unix时间当天日期
     * @param unixTime
     * @return
     */
    public static String unix2Day(String unixTime){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(unixTime) * 1000));
        return date;
    }

    /**
     * 获取中文格式的时间
     * @return
     */
    public static String getChineseDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    /**
     * 获取收藏时间 yyyy-MM-dd
     * @return
     */
    public static String getCollectionDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }
    /**
     * 资讯类时间显示
     */
    public static String getShowTime(String info_date){
        if(info_date == null || info_date.length() == 0){
            return "未知";
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayStr = df.format(new Date());
        if(info_date.substring(0,10).equals(todayStr.substring(0,10))){
            return info_date.substring(11);
        }else {
            return info_date.substring(0,10);
        }

       /* try {
            Date info = df.parse(info_date);
            Date now = new Date();
            if(now.getTime() - info.getTime() < 24*60*60*1000){
                return info_date.substring(11);
            }else {
                return info_date.substring(0,10);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return info_date.substring(0,10);
        }*/
    }


    /**判断时间格式 格式必须为“YYYY-MM-dd”
     * @param str
     * @return true-是标准时间格式
     * 			false-不是标准时间格式
     */
    public static boolean isValidDate(String str) {
        //String str = "2007-01-02";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date = (Date)formatter.parse(str);
            return str.equals(formatter.format(date));
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 获取当天0点的时间戳
     * @return
     */
    public static int getTimesmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis()/1000);
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static int getCurrentTimes(){
        Calendar cal = Calendar.getInstance();
        return (int)(cal.getTimeInMillis()/1000);
    }
    /**
     * 获取本周一0点时间戳
     * @return
     */
    public static int getTimesWeekmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis()/1000);
    }

    /**
     * 获得本月第一天0点时间
     */
    public static int getTimesMonthmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), 1, 0, 0,0);
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return (int) (cal.getTimeInMillis()/1000);
    }

    /**
     * 当前年的开始时间，即 01-01 00:00:00
     *
     * @return
     */
    public  static int  getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),1, 1, 0, 0,0);
        cal.set(Calendar.DAY_OF_YEAR,cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        return (int) (cal.getTimeInMillis()/1000);
    }

}
