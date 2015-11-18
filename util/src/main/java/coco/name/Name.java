package coco.name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Name {

	private static Integer cRuntimeIdGenerator = 1009;

	private final Integer mRuntimeId;

	private final String mId;

	private final Map<Object, Object> mIdMap;

	public Name(String pId) {
		mId = pId;
		mIdMap = new HashMap<Object, Object>();
		synchronized (cRuntimeIdGenerator) {
			mRuntimeId = ++cRuntimeIdGenerator;
		}
		validate();
	}

	public final Integer getRuntimeId() {
		return mRuntimeId;
	}

	public final Collection<Object> getContexts() {
		return new ArrayList<Object>(mIdMap.keySet());
	}

	public final Collection<Object> getIds() {
		Collection<Object> ids = new ArrayList<Object>(mIdMap.values());
		ids.add(mId);
		return ids;
	}

	public final void addId(Object pId, Object pContextId) {
		synchronized (this) {
			if (pContextId == null) {
				throw new RuntimeException("context cannot be null: "
						+ pContextId + "/" + getId());
			}
			if (mIdMap.get(pContextId) != null) {
				throw new RuntimeException("context not unique: " + pContextId
						+ "/" + getId());
			}
			if (pId == null) {
				throw new RuntimeException("id cannot be null: " + getId());
			}
			mIdMap.put(pContextId, pId);
		}
	}

	public final Object getId(Object pContext) {
		return mIdMap.get(pContext);
	}

	public final String getId() {
		return mId;
	}

	public String toString() {
		return mId;
	}

	private void validate() {
		if (mId == null) {
			throw new RuntimeException("id cannot be null");
		}
		if (mId.trim().equals("")) {
			throw new RuntimeException("id cannot be empty");
		}
	}
}
