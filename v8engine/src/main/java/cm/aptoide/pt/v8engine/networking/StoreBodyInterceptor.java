package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import okhttp3.RequestBody;
import rx.Single;

/**
 * Created by marcelobenites on 13/03/17.
 */

public class StoreBodyInterceptor implements BodyInterceptor<HashMapNotNull<String, RequestBody>> {

  private final AptoideAccountManager accountManager;
  private final RequestBodyFactory requestBodyFactory;

  public StoreBodyInterceptor(AptoideAccountManager accountManager,
      RequestBodyFactory requestBodyFactory) {
    this.accountManager = accountManager;
    this.requestBodyFactory = requestBodyFactory;
  }

  @Override public Single<HashMapNotNull<String, RequestBody>> intercept(
      HashMapNotNull<String, RequestBody> body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          body.put("access_token",
              requestBodyFactory.createBodyPartFromString(account.getAccessToken()));
          return Single.just(body);
        });
  }
}
