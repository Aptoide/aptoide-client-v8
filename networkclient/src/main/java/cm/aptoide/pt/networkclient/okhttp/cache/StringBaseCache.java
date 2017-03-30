package cm.aptoide.pt.networkclient.okhttp.cache;

/**
 * Created by sithengineer on 28/10/2016.
 */

abstract class StringBaseCache<K, V> extends BaseCache<K, V, String> {

  StringBaseCache(KeyAlgorithm<K, String> keyAlgorithm) {
    super(keyAlgorithm);
  }

  @Override public void put(K key, V value) {
    if (keyAlgorithm == null) {
      throw new UnsupportedOperationException("Initialize cache using init() first");
    }
    put(keyAlgorithm.getKeyFrom(key), value);
  }

  @Override public V get(K key) {
    if (keyAlgorithm == null) {
      throw new UnsupportedOperationException("Initialize cache using init() first");
    }
    if (isValid(key)) {
      return get(keyAlgorithm.getKeyFrom(key), key);
    }
    return null;
  }

  @Override public void remove(K key) {
    if (keyAlgorithm == null) {
      throw new UnsupportedOperationException("Initialize cache using init() first");
    }
    remove(keyAlgorithm.getKeyFrom(key));
  }

  @Override public boolean contains(K key) {
    if (keyAlgorithm == null) {
      throw new UnsupportedOperationException("Initialize cache using init() first");
    }
    return contains(keyAlgorithm.getKeyFrom(key));
  }

  @Override public boolean isValid(K key) {
    if (keyAlgorithm == null) {
      throw new UnsupportedOperationException("Initialize cache using init() first");
    }
    String keyAsString = keyAlgorithm.getKeyFrom(key);
    return (contains(keyAsString) && isValid(keyAsString));
  }

  abstract void put(String key, V value);

  abstract V get(String keyString, K keyObject);

  abstract boolean contains(String key);

  abstract boolean isValid(String key);

  abstract void remove(String key);
}
