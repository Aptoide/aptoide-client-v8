/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.CrashReports;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 23-05-2016.
 */
public abstract class BaseRequestWithStore<U, B extends BaseBodyWithStore> extends V7<U, B> {

  private static final String TAG = BaseRequestWithStore.class.getName();

  public BaseRequestWithStore(B body, String baseHost) {
    super(body, baseHost);
  }

  public BaseRequestWithStore(B body, Converter.Factory converterFactory,
      String baseHost) {
    super(body, converterFactory, baseHost);
  }

  public BaseRequestWithStore(B body, OkHttpClient httpClient, String baseHost) {
    super(body, httpClient, baseHost);
  }

  public BaseRequestWithStore(B body, OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
  }

  /**
   * Create non-static method that uses Accessors.
   */
  @Deprecated protected static StoreCredentials getStore(String storeName) {
    //@Cleanup Realm realm = DeprecatedDatabase.get();
    //if (storeName != null) {
    //  Store store = DeprecatedDatabase.StoreQ.get(storeName, realm);
    //  if (store != null) {
    //    return new StoreCredentialsApp(store.getUsername(), store.getPasswordSha1());
    //  }
    //}
    //return new StoreCredentialsApp();

    if (!TextUtils.isEmpty(storeName)) {
      StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
      Store store = storeAccessor.get(storeName).toBlocking().firstOrDefault(null);
      if (store != null) {
        return new StoreCredentials(store.getUsername(), store.getPasswordSha1());
      }
    }

    return new StoreCredentials();
  }

  /**
   * Create non-static method that uses Accessors.
   */
  @Deprecated protected static StoreCredentials getStore(Long storeId) {
    //@Cleanup Realm realm = DeprecatedDatabase.get();
    //
    //if (storeId != null) {
    //  Store store = DeprecatedDatabase.StoreQ.get(storeId, realm);
    //  if (store != null) {
    //    return new StoreCredentialsApp(store.getUsername(), store.getPasswordSha1());
    //  }
    //}
    //return new StoreCredentialsApp();

    if (storeId != null) {
      StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
      Store store = storeAccessor.get(storeId).toBlocking().firstOrDefault(null);
      if (store != null) {
        return new StoreCredentials(store.getUsername(), store.getPasswordSha1());
      }
    }

    return new StoreCredentials();
  }

  @AllArgsConstructor public static class StoreCredentials {

    @Getter private String username;
    @Getter private String password;

    public StoreCredentials() {
      username = null;
      password = null;
    }
  }
}
