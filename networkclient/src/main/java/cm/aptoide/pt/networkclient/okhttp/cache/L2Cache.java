package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.crashreports.CrashReport;
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
  private final Pattern pattern = Pattern.compile("\\d+"); // 1 or more digits
  private volatile boolean isPersisting = false;
  private AtomicInteger persistenceCounter = new AtomicInteger(0);
  // can't be final due to de-serialization
  private ConcurrentHashMap<String, ResponseCacheEntry> cache;

  public L2Cache(KeyAlgorithm<Request, String> keyAlgorithm) {
    super(keyAlgorithm);
    cache = new ConcurrentHashMap<>(60);
    // 60 is a nice value since the cold boot of the app it does ~30 different requests

    try {
      load();
    } catch (IOException e) {
      //Logger.e(TAG, e);
      // do nothing in case of an IOException. Android File.fileExists() and File.canRead()
      // are not viable, so an exception is thrown in case the cache file does not exist
      // but that exception is not relevant to the common developer and only causes confusion
      // when reading the log.
    }
  }

  /**
   * loads data from file to memory
   */
  private void load() throws IOException {
    File cacheFile = new File(AptoideUtils.getContext().getCacheDir(), CACHE_FILE_NAME);
    //if(!cacheFile.exists() || !cacheFile.canRead()) return;

    cache = new ObjectMapper().readValue(cacheFile,
        new TypeReference<ConcurrentHashMap<String, ResponseCacheEntry>>() {
        });
    Logger.d(TAG, "Loaded cache file");
  }

  @Override public void destroy() {
    persist();
    cache.clear();
  }

  private void persist() {
    isPersisting = true;

    // remove invalid entries
    removeInvalid();

    // store in disk
    try {
      store();
    } catch (IOException e) {
      CrashReport.getInstance().log(e);
    }

    int value;
    do {
      value = persistenceCounter.get();
    } while (!persistenceCounter.compareAndSet(value, 0));

    isPersisting = false;
  }

  /**
   * clean invalid cache entries
   */
  private void removeInvalid() {
    for (Map.Entry<String, ResponseCacheEntry> cacheEntry : cache.entrySet()) {
      if (!cacheEntry.getValue().isValid()) {
        cache.remove(cacheEntry.getKey());
      }
    }
  }

  /**
   * snapshots current cache to avoid concurrent modifications and persists it
   */
  private void store() throws IOException {
    File cacheFile = new File(AptoideUtils.getContext().getCacheDir(), CACHE_FILE_NAME);
    new ObjectMapper().writeValue(cacheFile, cache);
    Logger.d(TAG, "Stored cache file");
  }

  @Override public void put(String key, Response response) {
    int seconds = shouldCacheUntil(response);
    if (seconds >= 1) {
      cache.put(key, new ResponseCacheEntry(response, seconds));
    }
  }

  @Override public Response get(String key, Request request) {
    ResponseCacheEntry response = cache.get(key);

    if (persistenceCounter.incrementAndGet() >= MAX_COUNT && response != null && !isPersisting) {
      persist();
    }
    return response.getResponse(request);
  }

  @Override public boolean contains(String key) {
    return cache.containsKey(key);
  }

  @Override public boolean isValid(String key) {
    ResponseCacheEntry cachedResponse = contains(key) ? cache.get(key) : null;
    if (cachedResponse != null) {
      return cachedResponse.isValid();
    }
    return false;
  }

  @Override void remove(String key) {
    if (contains(key)) {
      cache.remove(key);
    }
  }

  private int shouldCacheUntil(Response response) {
    try {
      Headers headers = response.headers();
      if (headers.size() <= 0) {
        Logger.d(TAG, "not caching the response due to empty headers");
        return 0;
      }

      List<String> cacheControlHeaders = headers.values(CACHE_CONTROL_HEADER);
      if (cacheControlHeaders.size() <= 0) {
        Logger.d(TAG, "not caching the response due to empty Cache-Control header");
        return 0;
      }

      for (String headerValue : cacheControlHeaders) {
        if (headerValue.startsWith("max-age") || headerValue.startsWith("s-maxage")) {
          int seconds = extractNumber(headerValue);
          return seconds;
        }
      }
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
    }

    return 0;
  }

  private int extractNumber(String value) {
    Matcher matcher = pattern.matcher(value);
    if (matcher.find()) {
      String group = matcher.group(matcher.groupCount());
      return Integer.parseInt(group);
    }
    return 0;
  }

  public void clean() {
    if (cache != null && cache.size() > 0) {
      cache.clear();
    }
  }
}
