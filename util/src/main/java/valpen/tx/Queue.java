package valpen.tx;

import geo.coco.log.LogUtil;

import java.util.LinkedList;

public class Queue<E> implements Transmitter<E> {

	private final Receiver<E> mReceiver;

	private final LinkedList<E> mQueue = new LinkedList<E>();

	private final boolean mSingleThread;

	private int mMaxSize = Integer.MAX_VALUE;

	private Boolean mRunning = true;

	private Boolean mStarted = false;

	private Boolean mStopped = false;

	private boolean mFlag1 = false, mFlag2 = false;

	public Queue(Receiver<E> pReceiver, String pName, long pStartupTimeout,
			boolean pSingleThread) {
		if (pReceiver == null) {
			throw new NullPointerException();
		}
		mReceiver = pReceiver;
		mSingleThread = pSingleThread;
		if (!mSingleThread) {
			if (pName != null) {
				new Thread(new DispatchingThread(), pName).start();
			} else {
				new Thread(new DispatchingThread()).start();
			}
			if (pStartupTimeout > 0) {
				waitFor(pStartupTimeout);
			}
		} else {
			mStarted = true;
		}
	}

	public Queue(Receiver<E> pReceiver, long pStartupTimeout,
			boolean pSingleThread) {
		this(pReceiver, null, pStartupTimeout, pSingleThread);
	}

	public Queue(Receiver<E> pReceiver, boolean pSingleThread) {
		this(pReceiver, null, 10000, pSingleThread);
	}

	public Queue(Receiver<E> pReceiver, String pName, boolean pSingleThread) {
		this(pReceiver, pName, 10000, pSingleThread);
	}

	public void send(E pMessage) {
		send(pMessage, false);
	}

	public void send(E pMessage, boolean pVip) {
		if (!mStarted) {
			if (mFlag2) {
				return;
			}
			mFlag2 = true;
			throw new RuntimeException("queue not started");
		}
		if (mStopped) {
			if (mFlag1) {
				return;
			}
			mFlag1 = true;
			throw new RuntimeException("queue stopped");
		}
		if (mSingleThread) {
			mReceiver.receive(pMessage);
			return;
		}
		synchronized (mQueue) {
			if (pVip) {
				mQueue.addFirst(pMessage);
			} else {
				mQueue.addLast(pMessage);
			}
			mQueue.notify();
		}
	}

	public boolean isActive() {
		return mStarted && mRunning;
	}

	public void setMaxSize(int pMaxQueueLength) {
		mMaxSize = pMaxQueueLength;
	}

	public int size() {
		return mQueue.size();
	}

	public void stop() {
		mRunning = false;
		synchronized (mQueue) {
			mQueue.notify();
		}
	}

	protected boolean waitFor(long pTimeout) {
		long t = System.currentTimeMillis();
		while (!mStarted && System.currentTimeMillis() - t < pTimeout) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				LogUtil.getInstance().logThrowable(e);
			}
		}
		return mStarted;
	}

	private class DispatchingThread implements Runnable {
		public void run() {
			try {
				E tE;
				while (mRunning) {
					if (!mStarted) {
						mStarted = true;
					}
					synchronized (mQueue) {
						mQueue.wait();
					}
					while (!mQueue.isEmpty() && mRunning) {
						synchronized (mQueue) {
							tE = mQueue.removeFirst();
						}
						mReceiver.receive(tE);
						if (mQueue.size() > mMaxSize) {
							mRunning = false;
							throw new RuntimeException(
									"max queue length reached: "
											+ mQueue.size());
						}
					}
				}
			} catch (Exception e) {
				LogUtil.getInstance().logThrowable(e);
			}
			synchronized (mQueue) {
				mStopped = true;
				mQueue.clear();
			}
		}
	}
}
