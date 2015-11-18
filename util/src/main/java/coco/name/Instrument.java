package coco.name;

public class Instrument extends Name {

	private final Name mAsset;

	private final Name mCrncy;

	public Instrument(String pId, Name pAsset, Name pCrncy) {
		super(pId);
		mAsset = pAsset;
		mCrncy = pCrncy;
		validate();
	}

	public final Name getAsset() {
		return mAsset;
	}

	public final Name getCurncy() {
		return mCrncy;
	}

	private void validate() {
		if (mAsset == null) {
			throw new RuntimeException("asset cannot be null");
		}
		if (mCrncy == null) {
			throw new RuntimeException("currency cannot be null");
		}
	}
}
