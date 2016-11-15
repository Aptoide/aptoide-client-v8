package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  private static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static final String CACHE_CONTROL_HEADER = "Cache-Control";

  private static final int MAX_COUNT = 15;
  private volatile boolean isPersisting = false;
  private AtomicInteger persistenceCounter = new AtomicInteger(0);

  // can't be final due to de-serialization
  private ConcurrentHashMap<String, ResponseCacheEntry> cache;

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

  @Override public void destroy() {
    persist();
    cache.clear();
  }

  @Override void put(String key, Response response) {
    int seconds = shouldCacheUntil(response);
    if(seconds>=1){
      cache.put(key, new ResponseCacheEntry(response, seconds));
    }
  }

  private int shouldCacheUntil(Response response) {
    try{
      Headers headers = response.headers();
      if(headers.size()<=0) {
        Logger.d(TAG, "not caching the response due to empty headers");
        return 0;
      }

      List<String> cacheControlHeaders = headers.values(CACHE_CONTROL_HEADER);
      if(cacheControlHeaders.size()<=0) {
        Logger.d(TAG, "not caching the response due to empty Cache-Control header");
        return 0;
      }

      for(String headerValue : cacheControlHeaders) {
        if(headerValue.startsWith("max-age") || headerValue.startsWith("s-maxage")) {
          int seconds = extractNumber(headerValue);
          return seconds;
        }
      }
    } catch (Exception e) {
      Logger.e(TAG, e);
    }

    return 0;
  }

  private final Pattern pattern = Pattern.compile("\\d+"); // 1 or more digits
  private int extractNumber(String value) {
    Matcher matcher = pattern.matcher(value);
    if(matcher.find()) {
      String group = matcher.group(matcher.groupCount());
      return Integer.parseInt(group);
    }
    return 0;
  }

  @Override Response get(String key, Request request) {
    ResponseCacheEntry response = cache.get(key);

    if(persistenceCounter.incrementAndGet()>=MAX_COUNT && response!=null && !isPersisting){
      persist();
    }
    return response.getResponse(request);
  }

  private void persist() {
    isPersisting = true;

    // remove invalid entries
    removeInvalid();

    // store in disk
    try{
      store();
    } catch (IOException e) {
      Logger.e(TAG, e);
    }

    int value;
    do {
      value = persistenceCounter.get();
    } while (persistenceCounter.compareAndSet(value, 0));

    isPersisting = false;
  }

  @Override boolean contains(String key) {
    return cache.containsKey(key);
  }

  @Override public boolean isValid(String key) {
    ResponseCacheEntry cachedResponse = contains(key) ? cache.get(key) : null;
    if(cachedResponse!=null) {
      return cachedResponse.isValid();
    }
    return false;
  }

  @Override void remove(String key) {
    if(contains(key)) {
      cache.remove(key);
    }
  }

  /**
   * clean invalid cache entries
   */
  private void removeInvalid() {
    for(Map.Entry<String, ResponseCacheEntry> cacheEntry : cache.entrySet()){
      if(!cacheEntry.getValue().isValid()) {
        cache.remove(cacheEntry.getKey());
      }
    }
  }

  /**
   * snapshots current cache to avoid concurrent modifications and persists it
   */
  private void store() throws IOException {
    File cacheFile = new File(AptoideUtils.getContext().getCacheDir(), CACHE_FILE_NAME);

    String debug = new ObjectMapper().writeValueAsString(cache);

    new ObjectMapper().writeValue(cacheFile, cache);
    Logger.d(TAG, "Stored cache file");
  }

  /**
   * loads data from file to memory
   */
  private void load() throws IOException {
    File cacheFile = new File(AptoideUtils.getContext().getCacheDir(), CACHE_FILE_NAME);
    cache = new ObjectMapper().readValue(cacheFile,  new TypeReference<ConcurrentHashMap<String, ResponseCacheEntry>>(){});
    Logger.d(TAG, "Loaded cache file");
  }
}
