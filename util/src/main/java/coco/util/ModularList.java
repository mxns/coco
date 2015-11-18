package coco.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ModularList<E> implements List<E> {

	public final int length;

	private final E[] mList;

	private int mFirst = -1;

	private int mLast = -1;

	private boolean mFilled = false;

	public ModularList(E[] pList) {
		this(pList, pList.length);
	}

	public ModularList(E[] pList, int pLength) {
		mList = Arrays.copyOf(pList, pLength);
		Arrays.fill(mList, null);
		length = mList.length;
	}

	public boolean add(E e) {
		mLast = (mLast + 1) % mList.length;
		mList[mLast] = e;
		if (mFilled) {
			mFirst = (mFirst + 1) % mList.length;
		} else {
			mFirst = 0;
			if (mLast == mList.length - 1) {
				mFilled = true;
			}
		}
		return true;
	}

	public E get(int i) {
		if (mFirst < 0) {
			return null;
		}
		return mList[(mFirst + i) % mList.length];
	}

	public E first() {
		if (mFirst < 0) {
			return null;
		}
		return mList[mFirst];
	}

	public E last() {
		if (mLast < 0) {
			return null;
		}
		return mList[mLast];
	}

	public int size() {
		if (mFilled) {
			return length;
		}
		return mLast + 1;
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		mFirst = -1;
		mLast = -1;
		mFilled = false;
		Arrays.fill(mList, null);
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
}
