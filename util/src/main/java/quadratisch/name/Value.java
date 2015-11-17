package quadratisch.name;

public class Value {

	private final Name mName;

	private double mQuantity;

	public Value(Name pName) {
		mName = pName;
	}

	public void add(double pQuantity) {
		mQuantity += pQuantity;
	}

	public void set(double pQuantity) {
		mQuantity = pQuantity;
	}

	public double getQuantity() {
		return mQuantity;
	}

	public Name getName() {
		return mName;
	}

	public String getId() {
		return mName.getId();
	}

}
