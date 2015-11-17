package valpen.name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Names<E extends Name> {

	private final Map<Integer, E> mRuntimeIdMap = new HashMap<Integer, E>();

	private final Map<String, E> mIdMap = new HashMap<String, E>();

	private final Map<Object, Map<Object, E>> mContextMap = new HashMap<Object, Map<Object, E>>();

	private final CopyOnWriteArrayList<E> mAllNames = new CopyOnWriteArrayList<E>();

	public void addName(E pName) {
		synchronized (pName) {
			validate(pName);
			mIdMap.put(pName.getId(), pName);
			mRuntimeIdMap.put(pName.getRuntimeId(), pName);
			addContextIds(pName);
		}
		mAllNames.add(pName);
	}

	public boolean contains(E pName) {
		return mAllNames.contains(pName);
	}

	public Iterator<E> iterator() {
		return mAllNames.iterator();
	}

	public E get(Object pId) {
		E tN = queryId((String) pId);
		if (tN != null) {
			return tN;
		}
		Collection<Map<Object, E>> tC = mContextMap.values();
		for (Map<Object, E> tMap : tC) {
			tN = tMap.get(pId);
			if (tN != null) {
				return tN;
			}
		}
		return null;
	}

	public E queryContext(Object pContext, Object pId) {
		Map<Object, E> tMap = mContextMap.get(pContext);
		if (tMap != null) {
			return tMap.get(pId);
		}
		return null;
	}

	public E queryId(String pId) {
		return mIdMap.get(pId);
	}

	public Collection<E> queryAll(Object pId) {
		Collection<E> tList = new ArrayList<E>();
		queryAll(pId, tList);
		return tList;
	}

	public void queryAll(Object pId, Collection<E> pList) {
		E tN = queryId((String) pId);
		if (tN != null) {
			pList.add(tN);
		}
		Collection<Map<Object, E>> tC = mContextMap.values();
		for (Map<Object, E> tMap : tC) {
			tN = tMap.get(pId);
			if (tN != null) {
				pList.add(tN);
			}
		}
	}

	private void addContextIds(E pName) {
		Collection<Object> tContexts = pName.getContexts();
		Iterator<Object> tIter = tContexts.iterator();
		while (tIter.hasNext()) {
			Object tContextId = tIter.next();
			Object tIdInContext = pName.getId(tContextId);
			if (tIdInContext == null) {
				throw new RuntimeException(pName.getId()
						+ ": id null in context: " + tContextId);
			}
			Map<Object, E> tMap = mContextMap.get(tContextId);
			if (tMap == null) {
				tMap = new HashMap<Object, E>();
				mContextMap.put(tContextId, tMap);
			}
			Name tName = tMap.get(tIdInContext);
			if (tName != null && !tName.equals(pName)) {
				throw new RuntimeException(pName.getId() + ": id "
						+ tIdInContext + " not unique in context: "
						+ tContextId);
			}
			tMap.put(tIdInContext, pName);
		}
	}

	private void validate(E pName) {
		String tKey = pName.getId();
		if (mIdMap.get(tKey) != null) {
			throw new RuntimeException(tKey + ": key already in use: " + tKey);
		}
		if (mRuntimeIdMap.get(pName.getRuntimeId()) != null) {
			throw new RuntimeException(pName.getRuntimeId()
					+ ": key already in use: " + pName.getRuntimeId());
		}
		Collection<Object> tContexts = pName.getContexts();
		Iterator<Object> tIter = tContexts.iterator();
		while (tIter.hasNext()) {
			Object tContextId = tIter.next();
			Object tIdInContext = pName.getId(tContextId);
			Map<Object, E> tMap = mContextMap.get(tContextId);
			if (tMap == null) {
				continue;
			}
			Name tName = tMap.get(tIdInContext);
			if (tName != null && !tName.equals(pName)) {
				throw new RuntimeException(pName.getId() + ": id "
						+ tIdInContext + " not unique in context: "
						+ tContextId);
			}
		}
	}
}
