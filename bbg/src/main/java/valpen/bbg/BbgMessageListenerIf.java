package valpen.bbg;

import java.util.List;

import com.bloomberglp.blpapi.Message;

public interface BbgMessageListenerIf {

	public abstract void update(Message pMessage);

	public abstract void update(List<Message> pMessages);
}
