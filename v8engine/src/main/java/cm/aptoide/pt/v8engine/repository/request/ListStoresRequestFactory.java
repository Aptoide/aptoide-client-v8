package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 03-01-2017.
 */
class ListStoresRequestFactory {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public ListStoresRequestFactory(BodyInterceptor<BaseBody> baseBodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    this.bodyInterceptor = baseBodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return ListStoresRequest.ofTopStores(offset, limit, bodyInterceptor, httpClient,
        converterFactory);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return ListStoresRequest.ofAction(url, bodyInterceptor, httpClient, converterFactory);
  }
}
