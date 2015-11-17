package quadratisch.bbg;

import quadratisch.name.Name;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BbgMktDataService {

	public static enum TickerStatus {
		ACTIVE, INACTIVE, STALE, UNDEFINED
	}

	private Map<String, Double> mBbgPriceMultipliers = new HashMap<String, Double>();

	private final BbgSession mSession;

	private final Set<String> mSubscribedTickers = new HashSet<String>();

	public BbgMktDataService(BbgSession pSession) {
		mSession = pSession;
		validate();
	}

	public void setMultiplier(String pBbgTickerSymbol, double pMultiplier) {
		if (pMultiplier <= 0.0) {
			throw new RuntimeException("illegal multiplier: " + pMultiplier
					+ " for " + pBbgTickerSymbol);
		}
		if (pBbgTickerSymbol == null) {
			throw new RuntimeException("null not allowed");
		}
		mBbgPriceMultipliers.put(pBbgTickerSymbol, pMultiplier);
	}

	public void subscribeTo(String pBbgTickerSymbol,
			BbgMktDataListenerIf pMktDataListener, Name pName)
			throws IOException {
		BbgSubscriber tSubs = createSubscriptionListener(pName,
				pBbgTickerSymbol, pMktDataListener);
		List<BbgSubscriber> tList = new ArrayList<BbgSubscriber>();
		tList.add(tSubs);
		mSession.subscribe(tList);
	}

	public boolean isSubscribed(String pBbgTickerSymbol) {
		return mSubscribedTickers.contains(pBbgTickerSymbol);
	}

	private BbgSubscriber createSubscriptionListener(Name pName,
			String pBbgTickerSymbol, BbgMktDataListenerIf pMktDataListener) {
		if (mSubscribedTickers.contains(pBbgTickerSymbol)) {
			throw new RuntimeException("already subscribed: " + pName.getId());
		}
		Double tMultiplier = mBbgPriceMultipliers.get(pBbgTickerSymbol);
		if (tMultiplier == null) {
			tMultiplier = 1.0;
		}
		BbgMessageListenerIf tListener = new BbgMktDataSubscription(pName,
				pMktDataListener, tMultiplier);
		BbgSubscriber tSubsListener = new BbgSubscriber(pBbgTickerSymbol,
				"LAST_PRICE,SIZE_LAST_TRADE", tListener);
		mSubscribedTickers.add(pBbgTickerSymbol);
		return tSubsListener;
	}

	private void validate() {
		if (mSession == null) {
			throw new RuntimeException("session null");
		}
	}

	public static interface BbgMktDataListenerIf {

		public void updateLast(Name pName, double pPrice, double pSize,
				long pTimestamp);
	}

}
