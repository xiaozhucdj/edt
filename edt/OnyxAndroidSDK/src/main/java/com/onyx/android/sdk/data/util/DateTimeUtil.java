/**
 * 
 */
package com.onyx.android.sdk.data.util;

import android.app.AlarmManager;
import android.content.Context;
import android.text.format.DateFormat;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.data.GObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author joy
 *
 */
public class DateTimeUtil
{
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMM = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    
    /**
     * format the time according to the current locale and the user's 12-/24-hour clock preference
     * 
     * @param context
     * @return
     */
    public static String getCurrentTimeString(Context context) {
        return DateFormat.getTimeFormat(context).format(new Date());
    }
    
    public static String formatDate(Date date) {
    	if (date == null) {
    		return "";
    	}
		return DATE_FORMAT_YYYYMMDD_HHMMSS.format(date);
	}
    
    public static String formatDate(Date date, SimpleDateFormat simpleDateFormat) {
        if (date == null) {
            return "";
        }
        return simpleDateFormat.format(date);
    }
    
    public static String formatTime(Context context, long allSecond) {
		long hour_value = allSecond / 3600;
		long minute_value = allSecond % 3600 / 60;
		long second_value = allSecond % 3600 % 60;
		String whitespace_symbol = " ";
		
		String hour_symbol = context.getResources().getString(R.string.hour_symbol);
		String minute_symbol = context.getResources().getString(R.string.minute_symbol);
		String second_symbol = context.getResources().getString(R.string.second_symbol);

		if (hour_value > 0) {
			return hour_value + hour_symbol + whitespace_symbol + minute_value + minute_symbol;
		} else if (minute_value > 0) {
			return minute_value + minute_symbol + whitespace_symbol + second_value + second_symbol;
		} else {
			return second_value + second_symbol;
		}
	}

	public static boolean changeSystemTime(Context context, GObject object) {
		try {
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			am.setTime(object.getLong(GAdapterUtil.TAG_UNIQUE_ID));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean changeSystemTimeZone(Context context, GObject object) {
		return changeSystemTimeZone(context, object.getString(GAdapterUtil.TAG_UNIQUE_ID));
	}

	public static boolean changeSystemTimeZone(Context context, String key_id) {
		try {
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			am.setTimeZone(key_id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
