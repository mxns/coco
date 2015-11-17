package quadratisch.util;

import java.util.HashSet;
import java.util.Set;

public class TradingCalendar {

	private final Set<String> mHolidays = new HashSet<String>();

	public void addWorkingDays(Calendar pCalender, int pDelta) {
		int tCntr = 0;
		int tIncr = (int) Math.signum(pDelta);
		while (tCntr != pDelta) {
			pCalender.addDays(tIncr);
			if (isTradingDay(pCalender)) {
				tCntr += tIncr;
			}
		}
	}

	public void addHoliday(Calendar pCalendar) {
		int tYr = pCalendar.getYear();
		int tMt = pCalendar.getMonth();
		int tDt = pCalendar.getDay();
		String tKey = tYr + "-" + tMt + "-" + tDt;
		mHolidays.add(tKey);
	}

	public boolean isTradingDay(Calendar pCalendar) {
		if (pCalendar.isWeekend()) {
			return false;
		}
		int tYr = pCalendar.getYear();
		int tMt = pCalendar.getMonth();
		int tDt = pCalendar.getDay();
		String tKey = tYr + "-" + tMt + "-" + tDt;
		return !mHolidays.contains(tKey);
	}
}
