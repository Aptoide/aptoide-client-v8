package cm.aptoide.pt.v8engine;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.RequestBody;
import rx.Single;

/**
 * Created by marcelobenites on 13/03/17.
 */

public class StoreBodyInterceptor implements BodyInterceptor<HashMapNotNull<String, RequestBody>> {

  private final String aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private final RequestBodyFactory requestBodyFactory;
  private final String storeTheme;
  private final String storeDescription;
  private final ObjectMapper serializer;

  public StoreBodyInterceptor(String aptoideClientUUID, AptoideAccountManager accountManager,
      RequestBodyFactory requestBodyFactory, String storeTheme, String storeDescription,
      ObjectMapper serializer) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    this.requestBodyFactory = requestBodyFactory;
    this.storeTheme = storeTheme;
    this.storeDescription = storeDescription;
    this.serializer = serializer;
  }

  @Override public Single<HashMapNotNull<String, RequestBody>> intercept(
      HashMapNotNull<String, RequestBody> body) {
    return accountManager.accountStatus().first().toSingle().flatMap(account -> {
      try {
        body.put("store_properties", requestBodyFactory.createBodyPartFromString(
            serializer.writeValueAsString(
                new SimpleSetStoreRequest.StoreProperties(storeTheme, storeDescription))));
      } catch (JsonProcessingException e) {
        Single.error(e);
      }
      body.put("access_token",
          requestBodyFactory.createBodyPartFromString(account.getAccessToken()));

      return Single.just(body);
    });
  }
}
