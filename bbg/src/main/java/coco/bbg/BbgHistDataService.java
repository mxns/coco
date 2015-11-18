package coco.bbg;

import java.io.IOException;
import java.util.List;

import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.Request;

public class BbgHistDataService {

	private final BbgSession mSession;

	public BbgHistDataService(BbgSession pSession) {
		mSession = pSession;
	}

	public void requestHistoricalF160(String pFxSymbol, String pDate,
			BbgHistDataListenerIf pListener) throws IOException {
		requestHistoricalPrice(pFxSymbol + " F160 Curncy", pDate, pListener);
	}

	public void requestHistoricalPrice(String pSymbol, String pDate,
			BbgHistDataListenerIf pListener) throws IOException {
		if (mSession == null) {
			throw new RuntimeException("no session");
		}
		Request tRq = mSession.createRefDataRequest("HistoricalDataRequest");
		tRq.append("fields", "PX_LAST");
		tRq.append("fields", "OPEN");
		tRq.set("startDate", pDate);
		tRq.set("endDate", pDate);
		tRq.set("periodicitySelection", "DAILY");
		tRq.append("securities", pSymbol);
		mSession.sendRequest(tRq, new RspHandler(pListener));
	}

	private class RspHandler implements BbgMessageListenerIf {

		private final BbgHistDataListenerIf mListener;

		private RspHandler(BbgHistDataListenerIf pListener) {
			mListener = pListener;
		}

		@Override
		public void update(List<Message> pResponse) {
			for (Message tMsg : pResponse) {
				update(tMsg);
			}
		}

		@Override
		public void update(Message pMsg) {
			Element securityData = pMsg.getElement("securityData");
			Element fieldDataArray = securityData.getElement("fieldData");
			String tSymbol = securityData.getElementAsString("security");
			for (int j = 0; j < fieldDataArray.numValues(); ++j) {
				Element fieldData = fieldDataArray.getValueAsElement(j);
				double tLastPrice = fieldData.getElementAsFloat64("PX_LAST");
				String tDate = fieldData.getElementAsString("date");
				mListener.update(tSymbol, tLastPrice, tDate);
			}
		}
	}

	public static interface BbgHistDataListenerIf {
		public void update(String pSymbol, double pPrice, String pDate);
	}
}
