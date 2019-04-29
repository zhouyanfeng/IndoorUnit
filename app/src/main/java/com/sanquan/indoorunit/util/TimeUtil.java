package com.sanquan.indoorunit.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtils
 *
 */
public  class TimeUtil {

	    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		private static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy/MM/dd");
		private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
		private static final SimpleDateFormat NOTICE_DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy年MM月dd日");
	    private TimeUtil() {
	        throw new AssertionError();
	    }

	    /**
	     * long time to string
	     * 
	     * @param timeInMillis
	     * @param dateFormat
	     * @return
	     */
	    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
	        return dateFormat.format(new Date(timeInMillis));
	    }

	    /**
	     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
	     * 
	     * @param timeInMillis
	     * @return
	     */
	    public static String getTime(long timeInMillis) {
	        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
	    }
	    
	    

	    /**
	     * get current time in milliseconds
	     * 
	     * @return
	     */
	    public static long getCurrentTimeInLong() {
	        return System.currentTimeMillis();
	    }

	    /**
	     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
	     * 
	     * @return
	     */
	    public static String getCurrentTimeInString() {
	        return getTime(getCurrentTimeInLong());
	    }

	    /**
	     * get current time in milliseconds
	     * 
	     * @return
	     */
	    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
	    	if(dateFormat == null)
	    		return getTime(getCurrentTimeInLong(), TIME_FORMAT);
	    	
	        return getTime(getCurrentTimeInLong(), dateFormat);
	    }
	    
	    /*
	     * 
	     * 获取当前时间，格式DATE_FORMAT_DATE
	     * **/
	    public static String getCurTime(){
	    	 return getTime(getCurrentTimeInLong(), TIME_FORMAT);
	    }
	    
	    
	    /*
	     * 
	     * 获取当前日期，格式DATE_FORMAT_DATE
	     * **/
	    public static String getCurDate(){
	    	 return getTime(getCurrentTimeInLong(), DATE_FORMAT_DATE);
	    }

	    public static String getDate(long time){
	    	return getTime(time,NOTICE_DATE_FORMAT_DATE);
		}
	    /**
	     * 
	     * 日期  获取当前星期
	     * 
	     * */
	    public static String getWeekOfDate() {      
	        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};        
	        Calendar calendar = Calendar.getInstance();
	        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;      
	        if (w < 0){        
	            w = 0;      
	        }      
	        return weekOfDays[w];    
	    }
    
    /**
     * @return
     */
    public static long getMondayTimeInCurrentWeek(){
    	Calendar c = Calendar.getInstance();
    	int i = c.get(Calendar.DAY_OF_WEEK);
		if(i>=2){
			c.add(Calendar.DATE, 2-i);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}else if(i==1){
			c.add(Calendar.DATE, 1-7);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
    }
    /**
     * @return
     */
    public static long getMondayTimeInNextWeek(){
    	Calendar c = Calendar.getInstance();
    	int i = c.get(Calendar.DAY_OF_WEEK);
		if(i>=2){
			c.add(Calendar.DATE, 2+(7-i));
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}else if(i==1){
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
    }
    
    public static String parseLong2HmsTime(long timeMillis){
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(new Date(timeMillis));
    }
    /**
     * 
     * @param time  HH:mm:ss
     * @return
     */
    public static String floor(String time){
    	if(TextUtils.isEmpty(time)){
    		return "08:00:00";
    	}
    	String[] times = time.split(":");
    	int h = Integer.parseInt(times[0]);
    	if(h>8){
    		return "08:00:00";
    	}else{
    		return times[0]+":00:00";
    	}
       
    }
    public static String ceil(String time){
    	if(TextUtils.isEmpty(time)){
    		return "18:00:00";
    	}
    	String[] times = time.split(":");
    	int h = Integer.parseInt(times[0]);
    	int m = Integer.parseInt(times[1]);
    	if(m>0){
    		h++;
    	}
    	if(h<=18){
    		return "18:00:00";
    	}else{
    		String hour = String.format("%02d", h);
            return hour+":00:00";
    	}
    	
    }
}
