package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.RequestBody;
import rx.Single;
import rx.schedulers.Schedulers;

public class MultipartBodyInterceptor
    implements BodyInterceptor<HashMapNotNull<String, RequestBody>> {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final RequestBodyFactory requestBodyFactory;

  public MultipartBodyInterceptor(IdsRepository idsRepository,
      AptoideAccountManager accountManager, RequestBodyFactory requestBodyFactory) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.requestBodyFactory = requestBodyFactory;
  }

  @Override public Single<HashMapNotNull<String, RequestBody>> intercept(
      HashMapNotNull<String, RequestBody> body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          if (account.isLoggedIn()) {
            body.put("access_token",
                requestBodyFactory.createBodyPartFromString(account.getAccessToken()));
          }

          body.put("aptoide_uid",
              requestBodyFactory.createBodyPartFromString(idsRepository.getUniqueIdentifier()));

          return Single.just(body);
        })
        .subscribeOn(Schedulers.computation());
  }
}
