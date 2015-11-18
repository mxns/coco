package coco.bbg;

import java.util.List;

import com.bloomberglp.blpapi.Message;

public class BbgSubscriber {

	private final BbgMessageListenerIf mListener;

	private final String mTopic;

	private final String mOptions;

	public BbgSubscriber(String pTopic, String pOptions,
			BbgMessageListenerIf pListener) {
		mListener = pListener;
		mTopic = pTopic;
		mOptions = pOptions;
		validate();
	}

	public void update(Message pMessage) {
		mListener.update(pMessage);
	}

	public void update(List<Message> pMessages) {
		mListener.update(pMessages);
	}

	public String getTopic() {
		return mTopic;
	}

	public String getOptions() {
		return mOptions;
	}

	private final void validate() {
		if (mListener == null) {
			throw new RuntimeException("listener cannot be null");
		}
		if (mTopic == null) {
			throw new RuntimeException("topic cannot be null");
		}
		if (mOptions == null) {
			throw new RuntimeException("options cannot be null");
		}
	}
}
