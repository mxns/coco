package coco.bbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import com.bloomberglp.blpapi.Subscription;
import com.bloomberglp.blpapi.SubscriptionList;

public class BbgSession {

	private boolean mRunning = true;

	private boolean mStarted = false;

	private boolean mDebug;

	private Session mSession;

	private Long mCorrelationIdGenerator = new Long(1);

	private final Map<CorrelationID, ReqRspContainer> mReqRspMap = new HashMap<CorrelationID, ReqRspContainer>();

	private final Map<CorrelationID, BbgSubscriber> mSubscriptionMap = new HashMap<CorrelationID, BbgSubscriber>();

	private final Map<CorrelationID, BbgSubscriptionStatus> mSubscriptionStatusMap = new HashMap<CorrelationID, BbgSubscriptionStatus>();

	public void setDebug(boolean pDebug) {
		mDebug = pDebug;
	}

	public boolean isStarted() {
		return mStarted;
	}

	public void start() throws InterruptedException, IOException {
		createSession();
		openRefDataService();
		openMktDataService();
	}

	public void stop() {
		if (mSession != null) {
			// next line screws up the shutdown hook, do not use!
			// mSession.stop(Session.StopOption.ASYNC);
			mRunning = false;
		}
	}

	public void createSession() throws InterruptedException, IOException {
		if (mSession != null) {
			throw new RuntimeException("bbg session already started");
		}
		SessionOptions tOpts = new SessionOptions();
		tOpts.setServerHost("localhost"); // default value
		tOpts.setServerPort(8194); // default value
		mSession = new Session(tOpts);
		if (!mSession.start()) {
			mSession = null;
			throw new RuntimeException("could not start bbg session");
		}
		new Thread(new ReadThread()).start();
		mStarted = true;
	}

	public void openRefDataService() throws InterruptedException, IOException {
		if (mSession == null) {
			throw new RuntimeException("no bbg session");
		}
		if (!mSession.openService("//blp/refdata")) {
			throw new RuntimeException("could not open service //blp/refdata");
		}
	}

	public void openMktDataService() throws InterruptedException, IOException {
		if (mSession == null) {
			throw new RuntimeException("no bbg session");
		}
		if (!mSession.openService("//blp/mktdata")) {
			throw new RuntimeException("could not open service //blp/mktdata");
		}
	}

	public boolean hasPendingRequests() {
		synchronized (mSubscriptionStatusMap) {
			Iterator<BbgSubscriptionStatus> tIter = mSubscriptionStatusMap
					.values().iterator();
			while (tIter.hasNext()) {
				BbgSubscriptionStatus tRsp = tIter.next();
				if (tRsp.mSubscriptionStatus == null) {
					return true;
				}
			}
		}
		return false;
	}

	public Subscription createSubscription(String pSymbol, String pFields) {
		CorrelationID tCorrId;
		synchronized (mCorrelationIdGenerator) {
			tCorrId = new CorrelationID(++mCorrelationIdGenerator);
		}
		return new Subscription(pSymbol, pFields, tCorrId);
	}

	public Request createRefDataRequest(String pReqeust) {
		if (mSession == null) {
			throw new RuntimeException("no bbg session");
		}
		Service tRefDataService = mSession.getService("//blp/refdata");
		if (tRefDataService == null) {
			throw new RuntimeException("could not reach service //blp/refdata");
		}
		return tRefDataService.createRequest(pReqeust);
	}

	public void sendRequest(Request pRequest, BbgMessageListenerIf pListener)
			throws IOException {
		CorrelationID tCorrId;
		synchronized (mCorrelationIdGenerator) {
			tCorrId = new CorrelationID(++mCorrelationIdGenerator);
		}
		ReqRspContainer tRsp = new ReqRspContainer(pListener);
		mReqRspMap.put(tCorrId, tRsp);
		mSession.sendRequest(pRequest, tCorrId);
	}

	public void subscribe(List<BbgSubscriber> pSubscriptionListeners)
			throws IOException {
		SubscriptionList tList = new SubscriptionList();
		for (BbgSubscriber tListener : pSubscriptionListeners) {
			CorrelationID tCorrId;
			synchronized (mCorrelationIdGenerator) {
				tCorrId = new CorrelationID(++mCorrelationIdGenerator);
			}
			Subscription tSubscription = new Subscription(tListener.getTopic(),
					tListener.getOptions(), tCorrId);
			tList.add(tSubscription);
			mSubscriptionMap.put(tCorrId, tListener);
		}
		mSession.subscribe(tList);
	}

	private void handleSubscriptionEvent(Event pEvt, StringBuffer pReport) {
		MessageIterator tIter = pEvt.messageIterator();
		while (tIter.hasNext()) {
			Message tMsg = tIter.next();
			BbgSubscriber tSubscription = mSubscriptionMap.get(tMsg
					.correlationID());
			if (mDebug && pReport != null) {
				pReport.append("Topic: ");
				pReport.append(tSubscription.getTopic());
				pReport.append("\n").append(tMsg.toString());
			}
			tSubscription.update(tMsg);
		}
	}

	private void handlePartialResponseEvent(Event pEvent, StringBuffer pReport) {
		MessageIterator tIter = pEvent.messageIterator();
		while (tIter.hasNext()) {
			Message tMsg = tIter.next();
			CorrelationID tCorrId = tMsg.correlationID();
			ReqRspContainer tReqRsp = mReqRspMap.get(tCorrId);
			tReqRsp.mResponse.add(tMsg);
		}
	}

	private void handleResponseEvent(Event pEvent, StringBuffer pReport) {
		MessageIterator tIter = pEvent.messageIterator();
		while (tIter.hasNext()) {
			Message tMsg = tIter.next();
			if (mDebug && pReport != null) {
				pReport.append(tMsg.toString());
			}
			CorrelationID tCorrId = tMsg.correlationID();
			ReqRspContainer tReqRsp = mReqRspMap.remove(tCorrId);
			if (tReqRsp == null) {
				throw new RuntimeException("no request for correlation id: "
						+ tCorrId);
			}
			tReqRsp.mListener.update(tReqRsp.mResponse);
		}
	}

	private void handleOtherEvent(Event pEvent, StringBuffer pReport) {
		MessageIterator iter = pEvent.messageIterator();
		while (iter.hasNext()) {
			Message tMsg = iter.next();
			if (mDebug && pReport != null) {
				pReport.append(tMsg.toString());
			}
			if (Event.EventType.Constants.SESSION_STATUS == pEvent.eventType()
					.intValue()
					&& "SessionTerminated" == tMsg.messageType().toString()) {
				mRunning = false;
			}
			if (Event.EventType.Constants.SUBSCRIPTION_STATUS == pEvent
					.eventType().intValue()) {
				BbgSubscriber tSubscription = mSubscriptionMap.get(tMsg
						.correlationID());
				tSubscription.update(tMsg);
			}
		}
	}

	private static class BbgSubscriptionStatus {
		final String mBbgTicker;
		String mSubscriptionStatus;

		private BbgSubscriptionStatus(String pBbgTicker) {
			mBbgTicker = pBbgTicker;
			validate();
		}

		private void validate() {
			if (mBbgTicker == null) {
				throw new RuntimeException("bbg ticker cannot be null");
			}
		}

	}

	private static class ReqRspContainer {
		final List<Message> mResponse = new ArrayList<Message>();
		final BbgMessageListenerIf mListener;

		ReqRspContainer(BbgMessageListenerIf pListener) {
			mListener = pListener;
		}
	}

	private class ReadThread implements Runnable {
		public void run() {
			StringBuffer tSb = mDebug ? new StringBuffer() : null;
			while (mRunning) {
				try {
					if (mDebug) {
						tSb.setLength(0);
					}
					Event tEvt = mSession.nextEvent();
					switch (tEvt.eventType().intValue()) {
					case Event.EventType.Constants.SUBSCRIPTION_DATA:
						handleSubscriptionEvent(tEvt, tSb);
						break;
					case Event.EventType.Constants.RESPONSE:
						handlePartialResponseEvent(tEvt, tSb);
						handleResponseEvent(tEvt, tSb);
						break;
					case Event.EventType.Constants.PARTIAL_RESPONSE:
						handlePartialResponseEvent(tEvt, tSb);
						break;
					default:
						handleOtherEvent(tEvt, tSb);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
