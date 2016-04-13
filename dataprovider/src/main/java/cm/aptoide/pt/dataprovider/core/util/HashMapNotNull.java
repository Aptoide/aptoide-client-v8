package cm.aptoide.pt.dataprovider.core.util;

import java.util.HashMap;

/**
 * Created by neuro on 22-03-2016.
 */
public class HashMapNotNull<K, V> extends HashMap<K, V> {

	@Override
	public V put(K key, V value) {
		return value != null ? super.put(key, value) : value;
	}
}
