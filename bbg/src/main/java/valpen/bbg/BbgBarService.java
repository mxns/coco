package valpen.bbg;

import valpen.util.Calendar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.bloomberglp.blpapi.Datetime;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.Request;

public class BbgBarService {

	private final static TimeZone cUTC = TimeZone.getTimeZone("UTC");

	private final BbgSession mSession;

	public BbgBarService(BbgSession pSession) {
		mSession = pSession;
	}

	public void requestBars(String pInstrument, Date pStart, Date pStop,
			int pInterval, BbgBarListenerIf pListener) throws IOException,
			ParseException {
		java.util.Calendar tC = java.util.Calendar.getInstance();
		tC.setTime(pStart);
		int tYr = tC.get(java.util.Calendar.YEAR);
		int tMt = tC.get(java.util.Calendar.MONTH) + 1;
		int tDy = tC.get(java.util.Calendar.DATE);
		int tHr = tC.get(java.util.Calendar.HOUR_OF_DAY);
		int tMn = tC.get(java.util.Calendar.MINUTE);
		Datetime tStart = new Datetime(tYr, tMt, tDy, tHr, tMn, 0, 0);
		tC.setTime(pStop);
		tYr = tC.get(java.util.Calendar.YEAR);
		tMt = tC.get(java.util.Calendar.MONTH) + 1;
		tDy = tC.get(java.util.Calendar.DATE);
		tHr = tC.get(java.util.Calendar.HOUR_OF_DAY);
		tMn = tC.get(java.util.Calendar.MINUTE);
		Datetime tStop = new Datetime(tYr, tMt, tDy, tHr, tMn, 0, 0);
		requestIntradayBars(pInstrument, tStart, tStop, pInterval, pListener);
	}

	/**
	 * 
	 * @param pInstrument
	 *            BBG ticker symbol
	 * @param pStart
	 *            Start time on format: yyyy-MM-dd'T'HH:mm
	 * @param pStop
	 *            Stop time on format: yyyy-MM-dd'T'HH:mm
	 * @param pInterval
	 *            Bar length in minutes
	 * @param pListener
	 *            Listener
	 * @throws IOException
	 * @throws ParseException
	 */
	public void requestBars(String pInstrument, String pStart, String pStop,
			int pInterval, BbgBarListenerIf pListener) throws IOException,
			ParseException {
		SimpleDateFormat tSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		java.util.Calendar tC = java.util.Calendar.getInstance();
		tC.setTime(tSdf.parse(pStart));
		int tYr = tC.get(java.util.Calendar.YEAR);
		int tMt = tC.get(java.util.Calendar.MONTH) + 1;
		int tDy = tC.get(java.util.Calendar.DATE);
		int tHr = tC.get(java.util.Calendar.HOUR_OF_DAY);
		int tMn = tC.get(java.util.Calendar.MINUTE);
		Datetime tStart = new Datetime(tYr, tMt, tDy, tHr, tMn, 0, 0);
		tC.setTime(tSdf.parse(pStop));
		tYr = tC.get(java.util.Calendar.YEAR);
		tMt = tC.get(java.util.Calendar.MONTH) + 1;
		tDy = tC.get(java.util.Calendar.DATE);
		tHr = tC.get(java.util.Calendar.HOUR_OF_DAY);
		tMn = tC.get(java.util.Calendar.MINUTE);
		Datetime tStop = new Datetime(tYr, tMt, tDy, tHr, tMn, 0, 0);
		requestIntradayBars(pInstrument, tStart, tStop, pInterval, pListener);
	}

	/**
	 * 
	 * @param pSymbol
	 *            BBG ticker symbol
	 * @param pStart
	 *            Start time
	 * @param pStop
	 *            Stop time
	 * @param pInterval
	 *            Bar length in minutes
	 * @param pListener
	 *            Listener
	 * @throws IOException
	 */
	public void requestIntradayBars(String pSymbol, Datetime pStart,
			Datetime pStop, int pInterval, BbgBarListenerIf pListener)
			throws IOException {
		if (mSession == null) {
			throw new RuntimeException("no session");
		}
		Request tRq = mSession.createRefDataRequest("IntradayBarRequest");
		tRq.set("security", pSymbol);
		tRq.set("eventType", "TRADE");
		tRq.set("interval", pInterval);
		tRq.set("startDateTime", pStart);
		tRq.set("endDateTime", pStop);
		mSession.sendRequest(tRq, new RspHandler(pSymbol, pListener, pInterval));
	}

	public static interface BbgBarListenerIf {

		public void begin(String pId);

		public void end(String pId);

		public void addBar(String pId, int pInterval, double pOpen, double pHi,
				double pLo, double pClose, int pNumEvents, long pVolume,
				long pDatetime);
	}

	private static class RspHandler implements BbgMessageListenerIf {

		private final BbgBarListenerIf mBarCacheItem;

		private final String mId;

		private final int mInterval;

		private RspHandler(String pId, BbgBarListenerIf pBarCacheItem,
				int pInterval) {
			mBarCacheItem = pBarCacheItem;
			mId = pId;
			mInterval = pInterval;
		}

		@Override
		public void update(List<Message> pResponse) {
			for (Message tMsg : pResponse) {
				update(tMsg);
			}
		}

		@Override
		public void update(Message pMsg) {
			Element data = pMsg.getElement("barData").getElement("barTickData");
			int numBars = data.numValues();
			mBarCacheItem.begin(mId);
			for (int i = 0; i < numBars; ++i) {
				Element bar = data.getValueAsElement(i);
				Datetime time = bar.getElementAsDatetime("time");
				double open = bar.getElementAsFloat64("open");
				double high = bar.getElementAsFloat64("high");
				double low = bar.getElementAsFloat64("low");
				double close = bar.getElementAsFloat64("close");
				int numEvents = bar.getElementAsInt32("numEvents");
				long volume = bar.getElementAsInt64("volume");
				Calendar tCal = new Calendar(time.year(), time.month(),
						time.dayOfMonth(), time.hour(), time.minute(),
						time.second(), time.milliSecond(), cUTC);
				mBarCacheItem.addBar(mId, mInterval, open, high, low, close,
						numEvents, volume, tCal.getClockTimeMillis());
			}
			mBarCacheItem.end(mId);
		}
	}
}
