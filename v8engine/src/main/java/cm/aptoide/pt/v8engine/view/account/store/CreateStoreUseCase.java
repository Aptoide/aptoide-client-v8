package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * To create a store we need to call WS CheckUserCredentials so we can associate a
 * user to a newly created store.
 *
 * Then, if we have more data we either use a SetStore with multi-part request if we have
 * a store image, or a SetStore without image. This is the edit store use case {@link
 * EditStoreUseCase}.
 */
public class CreateStoreUseCase {

  private final ManageStoreViewModel storeModel;
  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;
  private BodyInterceptor<BaseBody> bodyInterceptorV3;

  public CreateStoreUseCase(ManageStoreViewModel storeModel, AptoideAccountManager accountManager,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      StoreBodyInterceptor bodyInterceptor, BodyInterceptor<BaseBody> bodyInterceptorV3) {
    this.storeModel = storeModel;
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  public Observable<Void> execute() {
    return createStore().flatMap(__ -> {
      if (needToUploadMoreStoreData()) {
        return new EditStoreUseCase(storeModel, accountManager, httpClient, converterFactory,
            bodyInterceptor).execute();
      }
      return Observable.empty();
    });
  }

  private boolean needToUploadMoreStoreData() {
    return storeModel.hasStoreAvatar() || storeModel.hasStoreDescription();
  }

  private Observable<Void> createStore() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> CheckUserCredentialsRequest.of(storeModel.getStoreName(),
            account.getAccessToken(), bodyInterceptorV3, httpClient, converterFactory)
            .observe())
        .map(__ -> null);
  }
}
