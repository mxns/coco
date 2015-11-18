package coco.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An object that maps keys to values. Furthermore, other keys (called "links")
 * can be mapped to several key/value pairs, so that a link may be used to
 * retrieve lists of values. Uses CopyOnWriteArrayList for the underlying
 * implementation, which means that <code>remove</code>, <code>link</code> and
 * <code>delink</code> will be costly operations, while <code>queryLink</code>
 * will be cheap. Note that the list returned by <code>queryLink</code> will be
 * thread safe (but not reflect any subsequent modifications to the underlying
 * map), while iterations over the collection returned by <code>values</code>
 * may throw exceptions if the MultiMap is simultaneously modified by
 * <code>put</code> or <code>remove</code>.
 * 
 * @param <L>
 *            Link type
 * @param <K>
 *            Key type
 * @param <V>
 *            Value type
 */
public class MultiMap<L, K, V> implements Map<K, V> {

	private final Map<K, V> mValues = new HashMap<K, V>();

	private final Map<L, CopyOnWriteArrayList<V>> mValueLists = new HashMap<L, CopyOnWriteArrayList<V>>();

	private final Map<K, CopyOnWriteArrayList<L>> mLinkLists = new HashMap<K, CopyOnWriteArrayList<L>>();

	/**
	 * Associates the specified value with the specified key in this map.
	 * 
	 * @see java.util.Map<K, V>
	 * @param key
	 *            - key with which the specified value is to be associated
	 * @param value
	 *            - value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key)
	 */
	public V put(K key, V value) {
		return mValues.put(key, value);
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key.
	 * 
	 * @see java.util.Map<K, V>
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	public V get(Object key) {
		return mValues.get(key);
	}

	/**
	 * Returns true if this map contains a mapping for the specified key.
	 * 
	 * @see java.util.Map<K, V>
	 * @param key
	 *            - key whose presence in this map is to be tested
	 * 
	 * @return true if this map contains a mapping for the specified key
	 */
	public boolean containsKey(Object key) {
		return mValues.containsKey(key);
	}

	/**
	 * Returns true if this map maps one or more keys to the specified value.
	 * 
	 * @see java.util.Map<K, V>
	 * @param value
	 *            - value whose presence in this map is to be tested
	 * 
	 * @return true if this map maps one or more keys to the specified value
	 */
	public boolean containsValue(Object value) {
		return mValues.containsValue(value);
	}

	/**
	 * Returns a Set view of the mappings contained in this map
	 * 
	 * @see java.util.Map<K, V>
	 * @return a set view of the mappings contained in this map
	 */
	public Set<Entry<K, V>> entrySet() {
		return mValues.entrySet();
	}

	/**
	 * Returns true if this map contains no key-value mappings.
	 * 
	 * @see java.util.Map<K, V>
	 * @return true if this map contains no key-value mappings
	 */
	public boolean isEmpty() {
		return mValues.isEmpty();
	}

	/**
	 * Returns a Set view of the keys contained in this map.
	 * 
	 * @see java.util.Map<K, V>
	 * @return a set view of the keys contained in this map
	 */
	public Set<K> keySet() {
		return mValues.keySet();
	}

	/**
	 * Returns a Set view of the links contained in this map.
	 * 
	 * @see java.util.Map<K, V>
	 * @return a set view of the links contained in this map
	 */
	public Set<L> linkSet() {
		return mValueLists.keySet();
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @see java.util.Map<K, V>
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return mValues.size();
	}

	/**
	 * Copies all of the mappings from the specified map to this map.
	 * 
	 * @see java.util.Map<K, V>
	 * @param m
	 *            - mappings to be stored in this map
	 */
	public void putAll(Map<? extends K, ? extends V> m) {
		mValues.putAll(m);
	}

	/**
	 * Removes the mapping for a key from this map if it is present. Also
	 * removes all links to the value associated with key.
	 * 
	 * @param key
	 *            - key whose mapping is to be removed from the map
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key
	 */
	public V remove(Object key) {
		V tNode = mValues.remove(key);
		if (tNode == null) {
			return null;
		}
		List<L> tLinks = mLinkLists.remove(key);
		if (tLinks == null) {
			return tNode;
		}
		for (L tLink : tLinks) {
			List<V> tNodes = mValueLists.get(tLink);
			if (tNodes == null) {
				continue;
			}
			tNodes.remove(tNode);
			if (tNodes.size() == 0) {
				mValueLists.remove(tLink);
			}
		}
		return tNode;
	}

	/**
	 * Returns a Collection view of the values contained in this map. Iterations
	 * over the collection may throw exceptions if the MultiMap is
	 * simultaneously modified by <code>put</code> or <code>remove</code>.
	 * 
	 * @return a collection view of the values contained in this map
	 */
	public Collection<V> values() {
		return mValues.values();
	}

	/**
	 * Associates the specified link with the specified key/value in this map.
	 * If there is no mapping for the specified key, the MultiMap is left
	 * unmodified. This method is synchronized to avoid unpredictable behaviour.
	 * 
	 * @param link
	 *            - link with which the specified key/value is to be associated
	 * 
	 * @param key
	 *            - key of the key/value pair with which the link is to be
	 *            associated
	 * @return the value associated with the specified key, or null if there was
	 *         no mapping for the key
	 */
	public synchronized V link(L link, K key) {
		V tNode = mValues.get(key);
		if (tNode == null) {
			return null;
		}
		CopyOnWriteArrayList<V> tNodes = mValueLists.get(link);
		if (tNodes == null) {
			tNodes = new CopyOnWriteArrayList<V>();
			mValueLists.put(link, tNodes);
		}
		if (!tNodes.contains(tNode)) {
			tNodes.add(tNode);
		}
		CopyOnWriteArrayList<L> tLinkList = mLinkLists.get(key);
		if (tLinkList == null) {
			tLinkList = new CopyOnWriteArrayList<L>();
			mLinkLists.put(key, tLinkList);
		}
		if (!tLinkList.contains(link)) {
			tLinkList.add(link);
		}
		return tNode;
	}

	/**
	 * Removes the association between a specified link and a specified
	 * key/value pair. If there is no mapping for the specified link, the
	 * MultiMap is left unmodified.
	 * 
	 * @param link
	 *            - the link whose association to a specified key/value pair is
	 *            to be removed
	 * @param key
	 *            - the key of the specified key/value pair
	 * @return the value associated with the specified key, or null if there was
	 *         no mapping for the key
	 */
	public V delink(L link, K key) {
		V tNode = mValues.get(key);
		if (tNode == null) {
			return null;
		}
		List<V> tNodes = mValueLists.get(link);
		if (tNodes == null) {
			return tNode;
		}
		tNodes.remove(tNode);
		if (tNodes.size() == 0) {
			mValueLists.remove(link);
		}
		return tNode;
	}

	/**
	 * Returns a thread safe List view of all values to which the specified link
	 * is mapped, or null if this map contains no mapping for the link. This
	 * list is a snapshot of the current mapping, and will not reflect any
	 * subsequent modifications to the underlying MultiMap.
	 * 
	 * @param link
	 *            - the link whose associated values is to be returned
	 * @return a list view of the values to which the specified link is
	 *         currently mapped, or null if this map contains no mapping for the
	 *         link
	 */
	public List<V> queryLink(L link) {
		return mValueLists.get(link);
	}

	/**
	 * Returns a thread safe List view of all links associated with the
	 * specified key/value pair, or null if this map contains no mapping for the
	 * link. This list is a snapshot of the current mapping, and will not
	 * reflect any subsequent modifications to the underlying MultiMap.
	 * 
	 * @param key
	 *            - the key in the key/value pair whose associated links is to
	 *            be returned
	 * @return a list view of the links currently associated to the specified
	 *         key/value pair, or null if this map contains no associations to
	 *         the key/value pair
	 */
	public List<L> getLinks(K key) {
		return mLinkLists.get(key);
	}

	@Override
	public void clear() {
		mValues.clear();
		mLinkLists.clear();
		mValueLists.clear();
	}
}
