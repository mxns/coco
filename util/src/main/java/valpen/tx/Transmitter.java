package valpen.tx;

public interface Transmitter<E> {

	public void send(E pMessage);

	public void send(E pMessage, boolean pVip);
}
