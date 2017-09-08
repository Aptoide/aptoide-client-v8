package cm.aptoide.pt.networking;

import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import okhttp3.RequestBody;
import rx.Single;
import rx.schedulers.Schedulers;

public class MultipartBodyInterceptor
    implements BodyInterceptor<HashMapNotNull<String, RequestBody>> {

  private final IdsRepository idsRepository;
  private final AuthenticationPersistence authenticationPersistence;
  private final RequestBodyFactory requestBodyFactory;

  public MultipartBodyInterceptor(IdsRepository idsRepository,
      RequestBodyFactory requestBodyFactory, AuthenticationPersistence authenticationPersistence) {
    this.idsRepository = idsRepository;
    this.authenticationPersistence = authenticationPersistence;
    this.requestBodyFactory = requestBodyFactory;
  }

  @Override public Single<HashMapNotNull<String, RequestBody>> intercept(
      HashMapNotNull<String, RequestBody> body) {
    return authenticationPersistence.getAuthentication()
        .flatMap(authentication -> {
          if (authentication.isAuthenticated()) {
            body.put("access_token",
                requestBodyFactory.createBodyPartFromString(authentication.getAccessToken()));
          }

          body.put("aptoide_uid",
              requestBodyFactory.createBodyPartFromString(idsRepository.getUniqueIdentifier()));

          return Single.just(body);
        })
        .subscribeOn(Schedulers.computation());
  }
}
