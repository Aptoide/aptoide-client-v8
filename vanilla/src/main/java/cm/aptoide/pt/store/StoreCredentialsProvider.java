package cm.aptoide.pt.store;

import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;

/**
 * Created by neuro on 03-01-2017.
 */

public interface StoreCredentialsProvider {

  BaseRequestWithStore.StoreCredentials get(long storeId);

  BaseRequestWithStore.StoreCredentials get(String storeName);

  @Nullable BaseRequestWithStore.StoreCredentials fromUrl(String url);
}
