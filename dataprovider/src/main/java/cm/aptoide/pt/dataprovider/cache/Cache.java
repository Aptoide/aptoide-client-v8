package cm.aptoide.pt.dataprovider.cache;

public interface Cache<K, V> {
  void put(K key, V value);

  V get(K key);

  void remove(K key);

  boolean contains(K key);

  boolean isValid(K key);

  void destroy();
}
