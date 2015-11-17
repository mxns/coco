package valpen.util;

import java.util.Calendar;
import java.util.TimeZone;

public class Timestamp {

	private int mHour;

	private int mMinute;

	private int mSecond;

	private int mMillis;

	private final TimeZone mTimeZone;

	public Timestamp(int hour, int minute, int second, int millis,
			TimeZone pTimezone) {
		mHour = hour;
		mSecond = second;
		mMinute = minute;
		mMillis = millis;
		mTimeZone = pTimezone;
	}

	public int getHour() {
		return mHour;
	}

	public int getMinute() {
		return mMinute;
	}

	public int getSecond() {
		return mSecond;
	}

	public int getMillis() {
		return mMillis;
	}

	public TimeZone getTimeZone() {
		return mTimeZone;
	}

	public int getClockTimeMillis() {
		return (mHour * 3600 + mMinute * 60 + mSecond) * 1000 + mMillis;
	}

	public boolean before(Timestamp pTimestamp) {
		return getClockTimeMillis() < pTimestamp.getClockTimeMillis();
	}

	public boolean after(Timestamp pTimestamp) {
		return getClockTimeMillis() > pTimestamp.getClockTimeMillis();
	}

	public boolean before(Calendar pCalendar) {
		int hour = pCalendar.get(java.util.Calendar.HOUR_OF_DAY);
		int min = pCalendar.get(java.util.Calendar.MINUTE);
		int sec = pCalendar.get(java.util.Calendar.SECOND);
		int mil = pCalendar.get(java.util.Calendar.MILLISECOND);
		return getClockTimeMillis() < (hour * 3600 + min * 60 + sec) * 1000
				+ mil;
	}

	public boolean after(Calendar pCalendar) {
		int hour = pCalendar.get(java.util.Calendar.HOUR_OF_DAY);
		int min = pCalendar.get(java.util.Calendar.MINUTE);
		int sec = pCalendar.get(java.util.Calendar.SECOND);
		int mil = pCalendar.get(java.util.Calendar.MILLISECOND);
		return getClockTimeMillis() > (hour * 3600 + min * 60 + sec) * 1000
				+ mil;
	}
}
