package cm.aptoide.pt.networkclient.okhttp.newCache;

/**
 * Created by sithengineer on 28/10/2016.
 */

public interface Cache<K,V> {
  void init(KeyAlgorithm keyAlgorithm);
  void put(K key, V value);
  boolean contains(K key);
  V get(K key);
}
