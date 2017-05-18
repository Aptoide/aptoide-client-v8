package cm.aptoide.pt.networkclient.okhttp.cache;

/**
 * Created on 28/10/2016.
 */

public abstract class BaseCache<K, V, Tout> implements Cache<K, V> {

  final KeyAlgorithm<K, Tout> keyAlgorithm;

  public BaseCache(KeyAlgorithm<K, Tout> keyAlgorithm) {
    this.keyAlgorithm = keyAlgorithm;
  }
}
