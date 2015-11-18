package coco.tx;

import geo.coco.log.LogUtil;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster<E> implements Transmitter<E> {

	private final CopyOnWriteArrayList<Transmitter<E>> mOutQueues = new CopyOnWriteArrayList<Transmitter<E>>();

	public void add(Transmitter<E> pTransmitter) {
		mOutQueues.add(pTransmitter);
	}

	public void send(E pMessage) {
		Iterator<Transmitter<E>> tIter = mOutQueues.iterator();
		while (tIter.hasNext()) {
			try {
				tIter.next().send(pMessage);
			} catch (Exception e) {
				LogUtil.getInstance().logThrowable(e);
			}
		}
	}

	public void send(E pMessage, boolean pVip) {
		Iterator<Transmitter<E>> tIter = mOutQueues.iterator();
		while (tIter.hasNext()) {
			try {
				tIter.next().send(pMessage, pVip);
			} catch (Exception e) {
				LogUtil.getInstance().logThrowable(e);
			}
		}
	}
}
