package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.RequestBody;
import rx.Single;

/**
 * Created by pedroribeiro on 30/05/17.
 */

public class UserBodyInterceptor implements BodyInterceptor<HashMapNotNull<String, RequestBody>> {

  private final AptoideAccountManager accountManager;
  private final RequestBodyFactory requestBodyFactory;
  private final String userName;
  private final ObjectMapper serializer;

  public UserBodyInterceptor(AptoideAccountManager accountManager,
      RequestBodyFactory requestBodyFactory, String userName, ObjectMapper serializer) {
    this.accountManager = accountManager;
    this.requestBodyFactory = requestBodyFactory;
    this.userName = userName;
    this.serializer = serializer;
  }

  @Override public Single<HashMapNotNull<String, RequestBody>> intercept(
      HashMapNotNull<String, RequestBody> body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          try {
            body.put("user_properties", requestBodyFactory.createBodyPartFromString(
                serializer.writeValueAsString(new SetUserRequest.UserProperties(userName))));
          } catch (JsonProcessingException e) {
            Single.error(e);
          }
          body.put("access_token",
              requestBodyFactory.createBodyPartFromString(account.getAccessToken()));
          return Single.just(body);
        });
  }
}
