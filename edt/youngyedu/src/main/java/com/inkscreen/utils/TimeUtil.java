package com.inkscreen.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @Title: 时间格式化操作<bar>
 * @Description:
 * @author liujian
 * @Date 2015年11月3日
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	static SimpleDateFormat dateFYMD = new SimpleDateFormat("yyyy-MM-dd");

	static SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static SimpleDateFormat dateFmA = new SimpleDateFormat("HH:mm");

	static SimpleDateFormat dateFmMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static SimpleDateFormat dateFmName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	static SimpleDateFormat timeFm = new SimpleDateFormat("mm:ss");
	static SimpleDateFormat dateMim = new SimpleDateFormat("HH:mm:ss");
	static SimpleDateFormat dateMD = new SimpleDateFormat("MM-dd HH:mm");

	/**
	 * 获取当前时间，默认返回格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String currentTime() {
		return dateFm.format(new Date()).toString();
	}

	/**
	 * 返回 HH:mm 格式的当前时间
	 * 
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String currentTimeByHour(long time) {
		return dateFmA.format(time).toString();
	}

	/**
	 * 返回 mm:ss 格式的当前时间
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatForSecond(long timestamp) {
		return  timeFm.format(timestamp);
	}

	/**
	 * 返回 yyyy-MM-dd HH:mm:ss 格式的时间
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatTime(long timestamp) {
		return dateFm.format(timestamp);
	}

	/**
	 * 以默认时间格式 yyyy-MM-dd HH:mm:ss 格式化时间
	 * 
	 * @param date
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	@SuppressWarnings("deprecation")
	public synchronized static String formatTime(String date) {
		if (date == null) {
			return "";
		}
		return dateFm.format(new Date(date)).toString();
	}

	/**
	 * 返回yyyy-MM-dd-HH-mm-ss 格式的时间以作为文件名保存文件
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatTimeForFileName(long timestamp) {
		return dateFmName.format(timestamp);
	}

	/**
	 * 返回 yyyy-MM-dd HH:mm 格式的时间
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatTimeForMinute(long timestamp) {
		return dateFmMin.format(timestamp);
	}

	/**
	 * 返回 yyyy-MM-dd 格式的时间
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatTimeForDay(long timestamp) {
		return dateFYMD.format(timestamp);
	}

	/**
	 * HH:mm:ss 格式化时间
	 * 
	 * @param timestamp
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatTimeMim(long timestamp) {
		return dateMim.format(timestamp);
	}

	/**
	 * 将时间string转成long,支持格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param time
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static long getTime(String time) {
		try {
			return dateFm.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将时间string转成long,支持格式"yyyy-MM-dd"
	 * 
	 * @param time
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static long getdateTime(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 返回yyyy-mm-dd
	 * 
	 * @param time
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatToYMD(String time) {
		String str = "";
		try {
			str = dateFYMD.format(dateFm.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 返回mm-dd hh-mm
	 * 
	 * @param time
	 * @return
	 * @author elanking
	 * @Date 2015年10月27日
	 */
	public synchronized static String formatToMD(long time) {
		return dateMD.format(time);
	}
}
