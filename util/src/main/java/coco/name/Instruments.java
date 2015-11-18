package coco.name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: make thread safe by synchronizing all maps and collections (performance
 * is not an issue since queries and changes should be sparse). Ensure that the
 * data is always consistent by validating all in-data before updating the
 * internal structure.
 * 
 * @author mda
 * 
 */
public class Instruments {

	private final Names<Instrument> mInstruments = new Names<Instrument>();

	private final Names<Name> mAssets = new Names<Name>();

	private final Map<CrossKey, Instrument> mCrossMap = new HashMap<CrossKey, Instrument>();

	public void addInstrument(Instrument pName) {
		mInstruments.addName(pName);
		if (!mAssets.contains(pName.getAsset())) {
			mAssets.addName(pName.getAsset());
		}
		if (!mAssets.contains(pName.getCurncy())) {
			mAssets.addName(pName.getCurncy());
		}
	}

	public void addCross(Instrument pInstrument) {
		CrossKey key = new CrossKey(pInstrument.getAsset(),
				pInstrument.getCurncy());
		if (mCrossMap.containsKey(key)) {
			throw new RuntimeException("cross not unique: " + key
					+ "; instrument: " + pInstrument.getId());
		}
		addInstrument(pInstrument);
		mCrossMap.put(key, pInstrument);
	}

	public Collection<Instrument> getAllInstruments() {
		Collection<Instrument> tList = new ArrayList<Instrument>();
		Iterator<Instrument> tIter = mInstruments.iterator();
		while (tIter.hasNext()) {
			tList.add(tIter.next());
		}
		return tList;
	}

	public Instrument queryCross(Name pN1, Name pN2) {
		if (pN1 == null || pN2 == null) {
			return null;
		}
		Instrument tCross = mCrossMap.get(new CrossKey(pN1, pN2));
		if (tCross == null) {
			tCross = mCrossMap.get(new CrossKey(pN2, pN1));
		}
		return tCross;
	}

	public Instrument queryCross(String pN1, String pN2) {
		Name tLong = mInstruments.queryId(pN1);
		Name tShort = mInstruments.queryId(pN2);
		return queryCross(tLong, tShort);
	}

	public Instrument queryCross(Object pN1, Object pN2, Object pContext) {
		Name tLong = mInstruments.queryContext(pContext, pN1);
		Name tShort = mInstruments.queryContext(pContext, pN2);
		return queryCross(tLong, tShort);
	}

	public Instrument queryCross(Object pN1, Object pContext1, Object pN2,
			Object pContext2) {
		Name tLong = mInstruments.queryContext(pContext1, pN1);
		Name tShort = mInstruments.queryContext(pContext2, pN2);
		return queryCross(tLong, tShort);
	}

	public Instrument queryContext(Object pContext, Object pId) {
		return mInstruments.queryContext(pContext, pId);
	}

	public Instrument queryId(String pId) {
		return mInstruments.queryId(pId);
	}

	public Collection<Instrument> queryAll(Object pId) {
		return mInstruments.queryAll(pId);
	}

	public void queryAll(Object pId, Collection<Instrument> pList) {
		mInstruments.queryAll(pId, pList);
	}

	private static class CrossKey {
		private final Name mLong;
		private final Name mShort;

		private CrossKey(Name pLong, Name pShort) {
			mLong = pLong;
			mShort = pShort;
		}

		public boolean equals(Object pObj) {
			if (pObj instanceof CrossKey) {
				CrossKey c = (CrossKey) pObj;
				return this.mLong == c.mLong && this.mShort == c.mShort;
			}
			return false;
		}

		public int hashCode() {
			return 0;
		}

		public String toString() {
			return mLong.getId() + " / " + mShort.getId();
		}
	}
}
