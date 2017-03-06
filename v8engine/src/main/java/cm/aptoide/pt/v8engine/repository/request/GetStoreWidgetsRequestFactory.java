package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 03-01-2017.
 */
class GetStoreWidgetsRequestFactory {

  private final AptoideAccountManager accountManager;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyDecorator bodyDecorator;

  public GetStoreWidgetsRequestFactory(AptoideAccountManager accountManager,
      StoreCredentialsProvider storeCredentialsProvider, BodyDecorator bodyDecorator) {
    this.accountManager = accountManager;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyDecorator = bodyDecorator;
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url),
        accountManager.getAccessToken(), bodyDecorator);
  }
}
