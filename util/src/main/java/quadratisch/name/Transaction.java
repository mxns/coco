package quadratisch.name;

import java.text.NumberFormat;

public class Transaction {

	private final double mQuantity;
	private final Instrument mInstrument;
	private final double mPrice;

	public Transaction(double pQuantity, Instrument pInstrument, double pPrice) {
		mQuantity = pQuantity;
		mInstrument = pInstrument;
		mPrice = pPrice;
		validate();
	}

	public double getQuantity() {
		return mQuantity;
	}

	public Instrument getInstrument() {
		return mInstrument;
	}

	public double getPrice() {
		return mPrice;
	}

	public String toString() {
		NumberFormat cFormat = NumberFormat.getInstance();
		cFormat.setMaximumFractionDigits(12);
		StringBuffer tSb = new StringBuffer();
		String tQuantity = cFormat.format(Math.abs(mQuantity));
		if (mQuantity > 0) {
			tSb.append("Buy ").append(tQuantity);
		} else if (mQuantity < 0) {
			tSb.append("Sell ").append(tQuantity);
		} else {
			tSb.append("0");
		}
		tSb.append(" ").append(mInstrument.getId());
		tSb.append("@").append(cFormat.format(mPrice));
		tSb.append(mInstrument.getCurncy().getId());
		return tSb.toString();
	}

	private void validate() {
		if (mInstrument == null) {
			throw new RuntimeException("instrument cannot be null");
		}
	}

}
