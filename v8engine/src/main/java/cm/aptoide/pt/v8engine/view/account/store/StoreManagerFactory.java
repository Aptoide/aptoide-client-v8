package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;

class StoreManagerFactory {
  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7;
  private final RequestBodyFactory requestBodyFactory;
  private final ObjectMapper objectMapper;

  public StoreManagerFactory(AptoideAccountManager accountManager, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7,
      RequestBodyFactory requestBodyFactory, ObjectMapper objectMapper) {
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.multipartBodyInterceptor = multipartBodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.requestBodyFactory = requestBodyFactory;
    this.objectMapper = objectMapper;
  }

  public StoreManager create() {
    return new StoreManager(accountManager, httpClient, converterFactory, multipartBodyInterceptor,
        bodyInterceptorV3, bodyInterceptorV7, requestBodyFactory, objectMapper);
  }
}
