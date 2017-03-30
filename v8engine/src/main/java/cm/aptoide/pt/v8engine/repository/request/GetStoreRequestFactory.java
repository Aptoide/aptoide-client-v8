package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 03-01-2017.
 */
class GetStoreRequestFactory {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor bodyInterceptor;

  public GetStoreRequestFactory(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor bodyInterceptor) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
  }

  public GetStoreRequest newStore(String url) {
    return GetStoreRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor);
  }
}
