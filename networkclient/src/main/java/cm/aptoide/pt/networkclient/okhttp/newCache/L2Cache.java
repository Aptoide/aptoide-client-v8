package cm.aptoide.pt.networkclient.okhttp.newCache;

import android.os.Environment;
import cm.aptoide.pt.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/*

Cache-Control possible values (https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html)

cache-response-directive =
         "public"                               ; Section 14.9.1
       | "private" [ "=" <"> 1#field-name <"> ] ; Section 14.9.1
       | "no-cache" [ "=" <"> 1#field-name <"> ]; Section 14.9.1
       | "no-store"                             ; Section 14.9.2
       | "no-transform"                         ; Section 14.9.5
       | "must-revalidate"                      ; Section 14.9.4
       | "proxy-revalidate"                     ; Section 14.9.4
       | "max-age" "=" delta-seconds            ; Section 14.9.3 -> interesting
       | "s-maxage" "=" delta-seconds           ; Section 14.9.3 -> interesting
       | cache-extension                        ; Section 14.9.6


*/
public class L2Cache extends StringBaseCache<Request, Response> {
  private static final String TAG = L2Cache.class.getName();

  private static final String CACHE_FILE_NAME = "aptoide.cache";
  private static final String CACHE_CONTROL_HEADER = "Cache-Control";

  private static final int MAX_COUNT = 60;
  private volatile boolean isPersisting = false;
  private AtomicInteger persistenceCounter = new AtomicInteger(0);

  // can't be final due to de-serialization
  private ConcurrentHashMap<String, ResponseWrapper> cache;

  public L2Cache(KeyAlgorithm<Request, String> keyAlgorithm) {
    super(keyAlgorithm);
    cache = new ConcurrentHashMap<>(60);
    // 60 is a nice value since the cold boot of the app it does ~30 different requests

    try{
      load();
    } catch (Exception e) {
      Logger.e(TAG, e);
    }
  }

  @Override void put(String key, Response response) {
    int seconds = shouldCacheUntil(response);
    if(seconds>=1){
      cache.put(key, new ResponseWrapper(response, seconds));
    }
  }

  private int shouldCacheUntil(Response response) {
    try{
      Headers headers = response.headers();
      if(headers.size()<=0) {
        Logger.w(TAG, "not caching the response due to empty headers");
        return 0;
      }

      List<String> cacheControlHeaders = headers.values(CACHE_CONTROL_HEADER);
      if(cacheControlHeaders.size()<=0) {
        Logger.w(TAG, "not caching the response due to empty Cache-Control header");
        return 0;
      }

      for(String headerValue : cacheControlHeaders) {
        if(headerValue.startsWith("max-age") || headerValue.startsWith("s-maxage")) {
          int seconds = Integer.parseInt(
              headerValue.substring(headerValue.lastIndexOf('=')+1, headerValue.length()),
              10
          );
          return seconds;
        }
      }
    } catch (Exception e) {
      Logger.e(TAG, e);
    }

    return 0;
  }

  @Override Response get(String key) {
    ResponseWrapper response = cache.get(key);

    if(persistenceCounter.incrementAndGet()>=MAX_COUNT && response!=null && !isPersisting){
      isPersisting = true;

      // remove invalid entries
      removeInvalid();

      // store in disk
      store();

      int value;
      do {
        value = persistenceCounter.get();
      } while (persistenceCounter.compareAndSet(value, 0));

      isPersisting = false;
    }
    return response.getResponse();
  }

  @Override boolean contains(String key) {
    return cache.containsKey(key);
  }

  @Override public boolean isValid(String key) {
    ResponseWrapper cachedResponse = contains(key) ? cache.get(key) : null;
    if(cachedResponse!=null) {
      return cachedResponse.isValid();
    }
    return false;
  }

  /**
   * clean invalid cache entries
   */
  private void removeInvalid() {
    for(Map.Entry<String, ResponseWrapper> cacheEntry : cache.entrySet()){
      if(!cacheEntry.getValue().isValid()) {
        cache.remove(cacheEntry.getKey());
      }
    }
  }

  /**
   * snapshots current cache to avoid concurrent modifications and persists it
   */
  private void store() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(new File(Environment.getDataDirectory(), CACHE_FILE_NAME), cache);
    } catch (IOException e) {
      Logger.e(TAG, e);
    }
  }

  /**
   * loads data from file to memory
   */
  private void load() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      cache = objectMapper.readValue(new File(Environment.getDataDirectory(), CACHE_FILE_NAME), cache.getClass());
    } catch (IOException e) {
      Logger.e(TAG, e);
    }
  }

  private static final class ResponseWrapper {
    private final long validity;
    private final Response response;

    ResponseWrapper(Response response, int secondsToPersist) {
      this.response = response;
      this.validity = System.currentTimeMillis() + (secondsToPersist*1000);
    }

    public boolean isValid() {
      return System.currentTimeMillis() <= validity;
    }

    public Response getResponse() {
      return response;
    }
  }
}
