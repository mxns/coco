package valpen.util;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

public class Calendar implements Serializable {

	private static final long serialVersionUID = -8515925739380394269L;

	private final java.util.Calendar mCalendar;

	public Calendar(long pTime, TimeZone pTimeZone) {
		mCalendar = java.util.Calendar.getInstance(pTimeZone);
		mCalendar.setLenient(false);
		mCalendar.setTimeInMillis(pTime);
	}

	public Calendar(TimeZone pTimeZone) {
		this(System.currentTimeMillis(), pTimeZone);
	}

	public Calendar(int pYear, int pMonth, int pDay, int pHour, int pMinute,
			int pSecond, int pMillis, TimeZone pTimeZone) {
		mCalendar = java.util.Calendar.getInstance(pTimeZone);
		mCalendar.setLenient(false);
		mCalendar.set(java.util.Calendar.YEAR, pYear);
		mCalendar.set(java.util.Calendar.MONTH, pMonth - 1);
		mCalendar.set(java.util.Calendar.DATE, pDay);
		mCalendar.set(java.util.Calendar.HOUR_OF_DAY, pHour);
		mCalendar.set(java.util.Calendar.MINUTE, pMinute);
		mCalendar.set(java.util.Calendar.SECOND, pSecond);
		mCalendar.set(java.util.Calendar.MILLISECOND, pMillis);
	}

	public Calendar(int pYear, int pMonth, int pDay, int pHour, int pMinute,
			TimeZone pTimeZone) {
		this(pYear, pMonth, pDay, pHour, pMinute, 0, 0, pTimeZone);
	}

	public Calendar(int pYear, int pMonth, int pDay, TimeZone pTimeZone) {
		this(pYear, pMonth, pDay, 0, 0, 0, 0, pTimeZone);
	}

	public java.util.Calendar getCalendar() {
		return mCalendar;
	}

	public void setTime(int pHour, int pMinute, int pSecond, int pMillis) {
		mCalendar.set(java.util.Calendar.HOUR_OF_DAY, pHour);
		mCalendar.set(java.util.Calendar.MINUTE, pMinute);
		mCalendar.set(java.util.Calendar.SECOND, pSecond);
		mCalendar.set(java.util.Calendar.MILLISECOND, pMillis);
	}

	public void set(int pYear, int pMonth, int pDay, int pHour, int pMinute,
			int pSecond, int pMillis) {
		mCalendar.set(java.util.Calendar.YEAR, pYear);
		mCalendar.set(java.util.Calendar.MONTH, pMonth - 1);
		mCalendar.set(java.util.Calendar.DATE, pDay);
		mCalendar.set(java.util.Calendar.HOUR_OF_DAY, pHour);
		mCalendar.set(java.util.Calendar.MINUTE, pMinute);
		mCalendar.set(java.util.Calendar.SECOND, pSecond);
		mCalendar.set(java.util.Calendar.MILLISECOND, pMillis);
	}

	public void addDays(int pDelta) {
		mCalendar.add(java.util.Calendar.DATE, pDelta);
	}

	public void addMinutes(int pDelta) {
		mCalendar.add(java.util.Calendar.MINUTE, pDelta);
	}

	public boolean isSameDate(java.util.Calendar pCalendar) {
		int tYr = pCalendar.get(java.util.Calendar.YEAR);
		int tMt = pCalendar.get(java.util.Calendar.MONTH);
		int tDt = pCalendar.get(java.util.Calendar.DATE);
		return mCalendar.get(java.util.Calendar.YEAR) == tYr
				&& mCalendar.get(java.util.Calendar.MONTH) == tMt
				&& mCalendar.get(java.util.Calendar.DATE) == tDt;
	}

	public boolean before(int pHour, int pMinute, int pSec, int pMillis) {
		return getClockTimeMillis() < (pHour * 3600 + pMinute * 60 + pSec)
				* 1000 + pMillis;
	}

	public boolean after(int pHour, int pMinute, int pSec, int pMillis) {
		return getClockTimeMillis() > (pHour * 3600 + pMinute * 60 + pSec)
				* 1000 + pMillis;
	}

	public boolean eq(int pHour, int pMinute, int pSec, int pMillis) {
		return getClockTimeMillis() == (pHour * 3600 + pMinute * 60 + pSec)
				* 1000 + pMillis;
	}

	public Date getDate() {
		return mCalendar.getTime();
	}

	public long getDateInMillis() {
		return mCalendar.getTimeInMillis();
	}

	public void setDateInMillis(long pMillis) {
		mCalendar.setTimeInMillis(pMillis);
	}

	public int getYear() {
		return mCalendar.get(java.util.Calendar.YEAR);
	}

	public int getMonth() {
		return mCalendar.get(java.util.Calendar.MONTH) + 1;
	}

	public int getDay() {
		return mCalendar.get(java.util.Calendar.DATE);
	}

	public int getHour() {
		return mCalendar.get(java.util.Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return mCalendar.get(java.util.Calendar.MINUTE);
	}

	public int getSecond() {
		return mCalendar.get(java.util.Calendar.SECOND);
	}

	public int getMillis() {
		return mCalendar.get(java.util.Calendar.MILLISECOND);
	}

	public int getClockTimeMillis() {
		return (getHour() * 3600 + getMinute() * 60 + getSecond()) * 1000
				+ getMillis();
	}

	public boolean isWeekend() {
		int tWd = mCalendar.get(java.util.Calendar.DAY_OF_WEEK);
		if (tWd == java.util.Calendar.SATURDAY) {
			return true;
		}
		if (tWd == java.util.Calendar.SUNDAY) {
			return true;
		}
		return false;
	}

	public String toString() {
		return mCalendar.getTime().toString();
	}
}
