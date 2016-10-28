package cm.aptoide.pt.networkclient.okhttp.newCache;

import java.util.LinkedHashMap;
import okhttp3.Request;

/**
 * Created by sithengineer on 28/10/2016.
 */

public class L2Cache extends StringBaseCache<Request, Request> {

  private LinkedHashMap<String, Request> cache;

  public L2Cache(KeyAlgorithm<Request, String> keyAlgorithm) {
    super(keyAlgorithm);
    cache = new LinkedHashMap<>(60);
    // 60 is a nice value since the cold boot of the app it does ~30 different requests
  }

  @Override void put(String key, Request value) {
    
  }

  @Override Request get(String key) {
    return null;
  }

  @Override boolean contains(String key) {
    return false;
  }

  @Override public boolean isValid(String key) {

    return false;
  }
}
