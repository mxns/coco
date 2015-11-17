package valpen.util;

import java.io.Serializable;

public class ProfitLoss implements Serializable {

	private static final long serialVersionUID = -4793099886000091571L;

	private double mAvgEntryPx;

	private double mAvgExitPx;

	private double mEntryQty;

	private double mExitQty;

	private double mRealizedPl;

	private final double mContractSz;

	public ProfitLoss() {
		this(1d);
	}

	public ProfitLoss(double pContractSz) {
		mContractSz = pContractSz;
	}

	public double getRealized() {
		return mRealizedPl;
	}

	public double getAvgEntryPrice() {
		return mAvgEntryPx;
	}

	public double getAvgExitPrice() {
		return mAvgExitPx;
	}

	public double getQty() {
		return mEntryQty + mExitQty;
	}

	public double getEntryQty() {
		return mEntryQty;
	}

	public double getExitQty() {
		return mExitQty;
	}

	public double evaluate(double pPrice) {
		if (pPrice == 0.0 || mAvgEntryPx == 0.0) {
			return 0;
		}
		double tCrntNetSz = mEntryQty + mExitQty;
		return tCrntNetSz * (pPrice - mAvgEntryPx) * mContractSz;
	}

	public double add(double pQuantity, double pPrice) {
		if (pQuantity == 0 || pPrice == 0) {
			return 0;
		}
		double tCrntNetSz = mEntryQty + mExitQty;
		boolean tProfitRealized = Math.abs(tCrntNetSz) > 0
				&& (Math.signum(tCrntNetSz) != Math.signum(pQuantity));
		boolean tSwitchedSign = oppositeSign(tCrntNetSz, tCrntNetSz + pQuantity);
		double tRealizedPnL = 0;
		if (tSwitchedSign) {
			tRealizedPnL = tCrntNetSz * (pPrice - mAvgEntryPx) * mContractSz;
			mAvgExitPx = 0;
			mExitQty = 0;
			mEntryQty = pQuantity - tCrntNetSz;
			mAvgEntryPx = pPrice;
		} else if (tProfitRealized) {
			mAvgExitPx = (mAvgExitPx * mExitQty + pQuantity * pPrice)
					/ (mExitQty + pQuantity);
			mExitQty += pQuantity;
			tRealizedPnL = -pQuantity * (pPrice - mAvgEntryPx) * mContractSz;
		} else {
			mAvgEntryPx = (mAvgEntryPx * tCrntNetSz + pQuantity * pPrice)
					/ (tCrntNetSz + pQuantity);
			mEntryQty += pQuantity;
		}
		mRealizedPl += tRealizedPnL;
		return tRealizedPnL;
	}

	static boolean oppositeSign(double pD1, double pD2) {
		if (pD1 == 0d || pD2 == 0) {
			return false;
		}
		if (Math.signum(pD1) == Math.signum(pD2)) {
			return false;
		}
		return true;
	}
}
