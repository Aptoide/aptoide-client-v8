package cm.aptoide.pt.v8engine.store;

import android.support.annotation.Nullable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreCredentialsProviderImpl implements StoreCredentialsProvider {

  private final StoreAccessor storeAccessor;

  public StoreCredentialsProviderImpl() {
    storeAccessor = AccessorFactory.getAccessorFor(Store.class);
  }

  @Override public BaseRequestWithStore.StoreCredentials get(long storeId) {

    Store store = storeAccessor.get(storeId)
        .toBlocking()
        .first();

    String username = null;
    String passwordSha1 = null;

    if (store != null) {
      username = store.getUsername();
      passwordSha1 = store.getPasswordSha1();
      return new BaseRequestWithStore.StoreCredentials(storeId, store.getStoreName(), username,
          passwordSha1);
    }

    return new BaseRequestWithStore.StoreCredentials(storeId, username, passwordSha1);
  }

  @Override public BaseRequestWithStore.StoreCredentials get(String storeName) {

    Store store = storeAccessor.get(storeName)
        .toBlocking()
        .first();

    String username = null;
    String passwordSha1 = null;

    if (store != null) {
      username = store.getUsername();
      passwordSha1 = store.getPasswordSha1();

      return new BaseRequestWithStore.StoreCredentials(store.getStoreId(), storeName, username,
          passwordSha1);
    }

    return new BaseRequestWithStore.StoreCredentials(storeName, username, passwordSha1);
  }

  @Nullable @Override public BaseRequestWithStore.StoreCredentials fromUrl(String url) {

    V7Url v7Url = new V7Url(url);
    Long storeId = v7Url.getStoreId();
    String storeName = v7Url.getStoreName();

    if (storeId != null) {
      return get(storeId);
    } else if (storeName != null) {
      return get(storeName);
    }

    return new BaseRequestWithStore.StoreCredentials();
  }
}
