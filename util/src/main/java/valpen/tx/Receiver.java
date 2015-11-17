package valpen.tx;

public interface Receiver<E> {

	public void receive(E pMessage);

}
