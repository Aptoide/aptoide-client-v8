package cm.aptoide.pt.store;

import androidx.annotation.Nullable;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreCredentialsProviderImpl implements StoreCredentialsProvider {

  private final RoomStoreRepository storeRepository;

  public StoreCredentialsProviderImpl(RoomStoreRepository storeRepository) {
    this.storeRepository = storeRepository;
  }

  @Override public BaseRequestWithStore.StoreCredentials get(long storeId) {

    RoomStore store = storeRepository.get(storeId)
        .toBlocking()
        .value();

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

    RoomStore store = storeRepository.get(storeName)
        .toBlocking()
        .value();

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
