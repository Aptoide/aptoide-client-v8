package cm.aptoide.pt.networkclient.okhttp.newCache;

/**
 * Created by sithengineer on 28/10/2016.
 */

public abstract class BaseCache<K, V> implements Cache<K,V> {

  KeyAlgorithm keyAlgorithm;

  @Override public void init(KeyAlgorithm keyAlgorithm) {
    this.keyAlgorithm = keyAlgorithm;
  }
}
