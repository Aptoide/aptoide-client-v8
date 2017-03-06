package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

/**
 * Created by neuro on 03-01-2017.
 */
class ListAppsRequestFactory {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyDecorator bodyDecorator;

  public ListAppsRequestFactory(BodyDecorator bodyDecorator,
      StoreCredentialsProvider storeCredentialsProvider) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyDecorator = bodyDecorator;
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyDecorator);
  }
}
