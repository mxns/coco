package coco.tx;

public interface Receiver<E> {

	public void receive(E pMessage);

}
