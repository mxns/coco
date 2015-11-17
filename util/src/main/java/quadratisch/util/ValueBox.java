package quadratisch.util;

import quadratisch.name.Instrument;
import quadratisch.name.Name;

import java.util.HashMap;
import java.util.Map;

public class ValueBox {

	private final Map<Integer, Value> mValueMap = new HashMap<Integer, Value>();

	private double mTotal;

	private final Name mName;

	public ValueBox(Name pName) {
		mName = pName;
	}

	/**
	 * Important: calls to this method must be externally synchronized, or
	 * simultaneous calls by different threads could result in a erroneous
	 * internal state.
	 */
	public Value add(Name pName, double pQuantity) {
		Value tD = mValueMap.get(pName.getRuntimeId());
		if (tD == null) {
			tD = new Value(pName);
			mValueMap.put(pName.getRuntimeId(), tD);
		}
		mTotal -= tD.getQuantity() * tD.mRate;
		tD.mQuantity += pQuantity;
		mTotal += tD.getQuantity() * tD.mRate;
		return tD;
	}

	/**
	 * Important: calls to this method must be externally synchronized, or
	 * simultaneous calls by different threads could result in a erroneous
	 * internal state.
	 */
	public Value add(Name pName, double pQuantity, double pRate) {
		Value tD = mValueMap.get(pName.getRuntimeId());
		if (tD == null) {
			tD = new Value(pName);
			mValueMap.put(pName.getRuntimeId(), tD);
		}
		mTotal -= tD.getQuantity() * tD.mRate;
		tD.mQuantity += pQuantity;
		tD.mRate = pRate;
		mTotal += tD.getQuantity() * tD.mRate;
		return tD;
	}

	/**
	 * Important: calls to this method must be externally synchronized, or
	 * simultaneous calls by different threads could result in a erroneous
	 * internal state.
	 */
	public Value setRate(Instrument pCross, double pRate) {
		double rate = 0;
		Value tD = null;
		if (pCross.getAsset() == mName) {
			tD = mValueMap.get(pCross.getCurncy().getRuntimeId());
			rate = 1 / pRate;
		} else if (pCross.getCurncy() == mName) {
			tD = mValueMap.get(pCross.getAsset().getRuntimeId());
			rate = pRate;
		}
		if (tD == null) {
			return null;
		}
		mTotal -= tD.getQuantity() * tD.mRate;
		tD.mRate = rate;
		mTotal += tD.getQuantity() * tD.mRate;
		return tD;
	}

	public double getTotal() {
		return mTotal;
	}

	public double getQuantity(Name pName) {
		Value tD = mValueMap.get(pName.getRuntimeId());
		if (tD == null) {
			return 0;
		}
		return tD.getQuantity();
	}

	public double getNormalizedQuantity(Name pName) {
		Value tD = mValueMap.get(pName.getRuntimeId());
		if (tD == null) {
			return 0;
		}
		return tD.getQuantity() * tD.mRate;
	}

	public static class Value {
		private final Name mValue;
		private double mQuantity;
		private double mRate;

		private Value(Name pName) {
			mValue = pName;
			mRate = 1;
		}

		public double getRate() {
			return mRate;
		}

		public double getQuantity() {
			return mQuantity;
		}

		public Name getName() {
			return mValue;
		}
	}
}
