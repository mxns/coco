package quadratisch.util;

import quadratisch.name.Instrument;
import quadratisch.name.Name;
import quadratisch.name.Transaction;
import quadratisch.name.Value;

public class Transactions {

	public static Transaction[] convertADR(Instrument pDepRct,
			Instrument pOrdinary, double pConversionQuantity,
			double pConversionPrice, double pConversionFactor,
			Instrument pFxCross, double pFxPrice) {
		double tOrdQuant = -pConversionQuantity * pConversionFactor;
		double tOrdPrice;
		double tFxQuant;
		if (pFxCross.getAsset() == pOrdinary.getCurncy()
				&& pFxCross.getCurncy() == pDepRct.getCurncy()) {
			tOrdPrice = (pConversionPrice / pConversionFactor) / pFxPrice;
			tFxQuant = -pConversionQuantity * pConversionPrice / pFxPrice;
		} else if (pFxCross.getAsset() == pDepRct.getCurncy()
				&& pFxCross.getCurncy() == pOrdinary.getCurncy()) {
			tOrdPrice = (pConversionPrice / pConversionFactor) * pFxPrice;
			tFxQuant = pConversionQuantity * pConversionPrice;
		} else {
			throw new RuntimeException("mismatch: " + pDepRct.getId() + " / "
					+ pOrdinary.getId() + " / " + pFxCross.getId());
		}
		Transaction t0 = new Transaction(pConversionQuantity, pDepRct,
				pConversionPrice);
		Transaction t1 = new Transaction(tOrdQuant, pOrdinary, tOrdPrice);
		Transaction t2 = new Transaction(tFxQuant, pFxCross, pFxPrice);
		Transaction[] tTransactions = new Transaction[3];
		tTransactions[0] = t0;
		tTransactions[1] = t1;
		tTransactions[2] = t2;
		return tTransactions;
	}

	public static Transaction[] convertORD(Instrument pDepRct,
			Instrument pOrdinary, double pConversionQuantity,
			double pConversionPrice, double pConversionFactor,
			Instrument pFxCross, double pFxPrice) {
		double tDrQuant = -pConversionQuantity / pConversionFactor;
		double tDrPrice;
		double tFxQuant;
		if (pFxCross.getAsset() == pOrdinary.getCurncy()
				&& pFxCross.getCurncy() == pDepRct.getCurncy()) {
			tDrPrice = pConversionPrice * pConversionFactor * pFxPrice;
			tFxQuant = pConversionQuantity * tDrPrice
					/ (pConversionFactor * pFxPrice);
		} else if (pFxCross.getAsset() == pDepRct.getCurncy()
				&& pFxCross.getCurncy() == pOrdinary.getCurncy()) {
			tDrPrice = pConversionPrice * pConversionFactor / pFxPrice;
			tFxQuant = -pConversionQuantity * tDrPrice / pConversionFactor;
		} else {
			throw new RuntimeException("mismatch: " + pDepRct.getId() + " / "
					+ pOrdinary.getId() + " / " + pFxCross.getId());
		}
		Transaction t0 = new Transaction(tDrQuant, pDepRct, tDrPrice);
		Transaction t1 = new Transaction(pConversionQuantity, pOrdinary,
				pConversionPrice);
		Transaction t2 = new Transaction(tFxQuant, pFxCross, pFxPrice);
		Transaction[] tTransactions = new Transaction[3];
		tTransactions[0] = t0;
		tTransactions[1] = t1;
		tTransactions[2] = t2;
		return tTransactions;
	}

	/**
	 * Construct the transaction that corresponds to buying
	 * <code>pQuantity</code> units of the asset <code>pAsset</code>, using the
	 * instrument <code>pInstrument</code>, when the market price of one unit of
	 * <code>pInstrument</code> is <code>pRate</code>. Note that
	 * <code>pQuantity</code> may be negative, which corresponds to
	 * <em>selling</em> <code>pAsset</code> instead of buying.
	 * 
	 * @param pQuantity
	 *            - traded quantity, positive for buying, negative for selling
	 * @param pAsset
	 *            - string id of asset that is traded
	 * @param pInstrument
	 *            - instrument that is used to trade the asset
	 * @param pRate
	 *            - market price of one unit of the instrument
	 * @return the transaction that will give the buyer/seller the specified
	 *         quantity of the specified asset, at a price determined by the
	 *         price of the instrument that is being used, or null if that
	 *         instrument cannot be used to trade the specified asset
	 */
	public static Transaction cross(double pQuantity, String pAsset,
			Instrument pInstrument, double pRate) {
		if (pInstrument.getAsset().getId().equals(pAsset)) {
			return new Transaction(pQuantity, pInstrument, pRate);
		} else if (pInstrument.getCurncy().getId().equals(pAsset)) {
			return new Transaction(-pQuantity * pRate, pInstrument, 1d / pRate);
		}
		return null;
	}

	/**
	 * Construct the transaction that corresponds to buying
	 * <code>pQuantity</code> units of the asset <code>pAsset</code>, using the
	 * instrument <code>pInstrument</code>, when the market price of one unit of
	 * <code>pInstrument</code> is <code>pRate</code>. Note that
	 * <code>pQuantity</code> may be negative, which corresponds to
	 * <em>selling</em> <code>pAsset</code> instead of buying.
	 * 
	 * @param pQuantity
	 *            - traded quantity, positive for buying, negative for selling
	 * @param pAsset
	 *            - asset that is traded
	 * @param pInstrument
	 *            - instrument that is used to trade the asset
	 * @param pRate
	 *            - market price of one unit of the instrument
	 * @return the transaction that will give the buyer/seller the specified
	 *         quantity of the specified asset, at a price determined by the
	 *         price of the instrument that is being used, or null if that
	 *         instrument cannot be used to trade the specified asset
	 */
	public static Transaction cross(double pQuantity, Name pAsset,
			Instrument pInstrument, double pRate) {
		if (pInstrument.getAsset() == pAsset) {
			return new Transaction(pQuantity, pInstrument, pRate);
		} else if (pInstrument.getCurncy() == pAsset) {
			return new Transaction(-pQuantity * pRate, pInstrument, 1d / pRate);
		}
		return null;
	}

	/**
	 * Convert a specified quantity of an asset (the source asset) to a quantity
	 * in another asset (the target asset), defined by the given instrument.
	 * 
	 * @param pQuantity
	 *            - the quantity that is being converted
	 * @param pAsset
	 *            - the asset that is being converted
	 * @param pInstrument
	 *            - the instrument that is being used to convert, defines the
	 *            target asset
	 * @param pRate
	 *            - market price of one unit of the instrument
	 * @return the quantity of the target asset
	 */
	public static double convert(double pQuantity, Name pAsset,
			Instrument pInstrument, double pRate) {
		if (pInstrument.getAsset() == pAsset) {
			return pQuantity * pRate;
		} else if (pInstrument.getCurncy() == pAsset) {
			return pQuantity / pRate;
		}
		return 0;
	}

	/**
	 * 
	 * @param pQuantity
	 *            - the quantity that is being converted
	 * @param pResult
	 *            - the resulting value, defines the target asset
	 * @param pInstrument
	 *            - the instrument that is being used to convert, defines the
	 *            source asset
	 * @param pRate
	 *            - market price of one unit of the instrument
	 */
	public static void convert(double pQuantity, Value pResult,
			Instrument pInstrument, double pRate) {
		if (pInstrument.getAsset() == pResult.getName()) {
			pResult.set(pQuantity / pRate);
		} else if (pInstrument.getCurncy() == pResult.getName()) {
			pResult.set(pQuantity * pRate);
		}
	}
}
