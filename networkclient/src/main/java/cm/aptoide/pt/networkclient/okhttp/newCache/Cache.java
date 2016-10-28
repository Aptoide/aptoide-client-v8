package cm.aptoide.pt.networkclient.okhttp.newCache;

/**
 * Created by sithengineer on 28/10/2016.
 */

public abstract class Cache<K, V, Tout> {

  final KeyAlgorithm<K, Tout> keyAlgorithm;
  public Cache(KeyAlgorithm<K, Tout> keyAlgorithm){
    this.keyAlgorithm = keyAlgorithm;
  }

  abstract void put(K key, V value);
  abstract V get(K key);
  abstract boolean contains(K key);
  abstract boolean isValid(K key);
}
