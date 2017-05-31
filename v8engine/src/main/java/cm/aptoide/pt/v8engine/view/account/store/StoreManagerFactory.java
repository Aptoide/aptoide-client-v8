package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

class StoreManagerFactory {
  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7;

  public StoreManagerFactory(AptoideAccountManager accountManager, OkHttpClient httpClient,
      Converter.Factory converterFactory, StoreBodyInterceptor bodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3, BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7) {
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.bodyInterceptorV7 = bodyInterceptorV7;
  }

  public StoreManager create(){
    return new StoreManager(accountManager, httpClient, converterFactory,
        bodyInterceptor, bodyInterceptorV3, bodyInterceptorV7);
  }
}
