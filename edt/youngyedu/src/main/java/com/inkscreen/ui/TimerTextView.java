package com.inkscreen.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 答题界面计时功能的textview
 * 
 * @author elanking
 *
 */
public class TimerTextView extends TextView {
	private long time = 0;// 秒
	private Handler handler = new Handler();
	boolean stop = true;
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			time = time + 1;
			TimerTextView.this.setText("用时:"+(getTime(time)));

//			Log.i("xcz","<<<"+getTime(time));

			handler.postDelayed(this, 1000);
			invalidate();
		}

	};

	public void setTime(long time)
	{
		this.time=time;
		setText("用时:"+(getTime(time)));
		//	setText("用时:"+(new Long(getTime(time))%3600/60)+"'"+(myTobeInfo.getStudentHomework().getHomeworkTime()%60));

	}
	public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TimerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimerTextView(Context context) {
		super(context);
	}

//	@Override
//	public void setText(CharSequence text, BufferType type) {
//		if (!TextUtils.isEmpty(text)) {
//
//			super.setText(text, type);
//
//			if (!stop) {
//				handler.postDelayed(runnable, 1000);
//			}
//		}
//	}

	public void stopTimer() {
		stop = true;
		handler.removeCallbacks(runnable);
	}

	public void startTimer() {
		if (stop) {
			stop = false;
			handler.postDelayed(runnable, 1000);
		}
	}

	public boolean isStop() {
		return stop;
	}

	private String getTime(long time) {
		long minutes = time / 60;
		long seconds = time % 60;
		return formatTime(minutes) + "'" + formatTime(seconds)+"''";
	}

	public long getTime() {
		return time;
	}

	// 00:00
	private String formatTime(long time) {
		if (String.valueOf(time).length() == 1) {
			return "0" + time;
		}
		return time + "";
	}
}
