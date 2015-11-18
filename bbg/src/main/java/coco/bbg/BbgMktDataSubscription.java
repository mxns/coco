package coco.bbg;

import coco.name.Name;

import java.util.List;

import com.bloomberglp.blpapi.Datetime;
import com.bloomberglp.blpapi.Message;

public class BbgMktDataSubscription implements BbgMessageListenerIf {
	private final BbgMktDataService.BbgMktDataListenerIf mCacheItem;
	private final Name mName;
	private final double mMultiplier;

	public BbgMktDataSubscription(Name pName, BbgMktDataService.BbgMktDataListenerIf pCacheItem,
			double pMultiplier) {
		mCacheItem = pCacheItem;
		mName = pName;
		mMultiplier = pMultiplier;
		validate();
	}

	private void getUpdates(Message pMessage) {
		double tLastPrice = 0, tLastVolume = 0;
		@SuppressWarnings("unused")
		Datetime tTimestamp = null, tSesStart = null, tSesEnd = null;
		try {
			if (pMessage.hasElement("LAST_PRICE", true)) {
				tLastPrice = pMessage.getElementAsFloat64("LAST_PRICE")
						* mMultiplier;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (pMessage.hasElement("SIZE_LAST_TRADE", true)) {
				tLastVolume = pMessage.getElementAsFloat64("SIZE_LAST_TRADE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (pMessage.hasElement("TRADE_UPDATE_STAMP_RT", true)) {
				tTimestamp = pMessage
						.getElementAsDatetime("TRADE_UPDATE_STAMP_RT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (tLastPrice > 0) {
			mCacheItem.updateLast(mName, tLastPrice, tLastVolume,
					System.currentTimeMillis());
		}
	}

	@Override
	public void update(Message pMessage) {
		getUpdates(pMessage);
	}

	@Override
	public void update(List<Message> mResponse) {
		for (Message tMsg : mResponse) {
			getUpdates(tMsg);
		}
	}

	private void validate() {
		if (mName == null) {
			throw new RuntimeException("no instrument");
		}
		if (mCacheItem == null) {
			throw new RuntimeException("no cache item");
		}
		if (mMultiplier == 0.0) {
			throw new RuntimeException("multiplier cannot be 0");
		}
	}
}