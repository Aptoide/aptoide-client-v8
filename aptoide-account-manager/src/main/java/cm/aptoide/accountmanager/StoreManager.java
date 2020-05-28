package cm.aptoide.accountmanager;

import rx.Completable;

/**
 * Created by trinkes on 11/12/2017.
 */

public interface StoreManager {
  Completable createOrUpdate(String storeName, String storeDescription, String storeImagePath,
      boolean hasNewAvatar, String storeThemeName, boolean storeExists);
}
