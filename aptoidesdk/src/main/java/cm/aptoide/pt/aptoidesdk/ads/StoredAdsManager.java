package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.pt.aptoidesdk.BuildConfig;
import cm.aptoide.pt.aptoidesdk.misc.ObjectMapperFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by neuro on 26-10-2016.
 */

public class StoredAdsManager {

  private static final String TAG = StoredAdsManager.class.getSimpleName();

  private static StoredAdsManager instance;

  private static ObjectMapper objectMapper =
      ObjectMapperFactory.getInstance().createDefaultObjectMapper();
  private static String ADS_REFERRERS_KEY = "AptoideSdkAdsReferrers";
  private static double AD_EXPIRATION_IN_MILLIS = 5 * 60 * 1000;
  private SharedPreferences sharedPreferences;

  private Map<Long, AptoideAd> ads;

  protected StoredAdsManager(Context context) {
    sharedPreferences = SecurePreferencesImplementation.getInstance(context);
    load();
  }

  public static StoredAdsManager getInstance(Context context) {
    if (instance == null) {
      synchronized (StoredAdsManager.class) {
        if (instance == null) {
          instance = new StoredAdsManager(context);
        }
      }
    }

    return instance;
  }

  public void addAd(AptoideAd ad) {
    ads.put(ad.id, ad);
    save();
  }

  public boolean hasAd(String packageName) {
    return getAd(packageName) != null;
  }

  public AptoideAd getAd(String packageName) {
    for (AptoideAd ad : ads.values()) {
      if (ad.getPackageName().equals(packageName)) {
        return ad;
      }
    }

    return null;
  }

  private void load() {
    try {
      String string = sharedPreferences.getString(ADS_REFERRERS_KEY, null);

      if (string != null) {
        ads = objectMapper.readValue(string, new TypeReference<HashMap<Long, AptoideAd>>() {
        });
        removeOldAds(ads);
      } else {
        ads = new HashMap<>();
      }
    } catch (JsonProcessingException e) {
      Logger.e(TAG, e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      sharedPreferences.edit()
          .putString(ADS_REFERRERS_KEY, objectMapper.writeValueAsString(ads))
          .apply();
    } catch (JsonProcessingException e) {
      Logger.e(TAG, e);
    }
  }

  private void removeOldAds(Map<Long, AptoideAd> ads) {

    Iterator<AptoideAd> iterator = ads.values().iterator();

    while (iterator.hasNext()) {
      AptoideAd ad = iterator.next();
      if (System.currentTimeMillis() - ad.timestamp > AD_EXPIRATION_IN_MILLIS) {
        iterator.remove();
      }
    }
  }

  public AptoideAd removeAd(String packageName) {
    AptoideAd ad = ads.remove(getAdId(packageName));
    save();
    return ad;
  }

  private Long getAdId(String packageName) {
    for (Map.Entry<Long, AptoideAd> adEntry : ads.entrySet()) {
      if (adEntry.getValue().getPackageName().equals(packageName)) {
        return adEntry.getKey();
      }
    }

    return null;
  }

  public void reload() {
    if (!BuildConfig.DEBUG) {
      throw new RuntimeException("reload() not allowed in production!");
    }
    load();
  }

  public int size() {
    return ads.size();
  }

  public Collection<AptoideAd> getAll() {
    return ads.values();
  }

  public void clear() {
    ads.clear();
    save();
  }
}
