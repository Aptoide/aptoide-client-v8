/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import cm.aptoide.pt.dataprovider.ws.RefreshBody;
import java.util.HashMap;

/**
 * Created by neuro on 22-03-2016.
 */
public class HashMapNotNull<K, V> extends HashMap<K, V> implements RefreshBody {

  @Override public V put(K key, V value) {
    return value != null ? super.put(key, value) : value;
  }

  @Override public void setRefresh(boolean refresh) {
  }
}
