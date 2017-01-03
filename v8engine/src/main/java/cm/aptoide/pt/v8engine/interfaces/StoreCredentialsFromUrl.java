package cm.aptoide.pt.v8engine.interfaces;

import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;

/**
 * Created by neuro on 03-01-2017.
 */

public interface StoreCredentialsFromUrl {
  BaseRequestWithStore.StoreCredentials get(String url);
}
