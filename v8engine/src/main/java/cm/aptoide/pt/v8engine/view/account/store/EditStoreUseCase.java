package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * If we have more data we either use a SetStore with multi-part request if we have
 * a store image, or a SetStore without image.
 */
public class EditStoreUseCase {

  private final ManageStoreModel storeModel;
  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;

  public EditStoreUseCase(ManageStoreModel storeModel, AptoideAccountManager accountManager,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      StoreBodyInterceptor bodyInterceptor) {
    this.storeModel = storeModel;
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
  }

  public Observable<Void> execute() {
    return Observable.defer(() -> {
      storeModel.prepareToSendRequest();
      if (storeModel.hasNewAvatar()) {
        return setStoreWithAvatar();
      }
      return setStoreWithoutAvatar();
    });
  }

  private Observable<Void> setStoreWithAvatar() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> SetStoreRequest.of(account.getAccessToken(), storeModel.getStoreName(),
            storeModel.getStoreThemeName(), storeModel.getStoreAvatarPath(),
            storeModel.getStoreDescription(), true, storeModel.getStoreId(), bodyInterceptor,
            httpClient, converterFactory)
            .observe())
        .map(__ -> null);
  }

  private Observable<Void> setStoreWithoutAvatar() {
    return SimpleSetStoreRequest.of(storeModel.getStoreId(), storeModel.getStoreThemeName(),
        storeModel.getStoreDescription(), bodyInterceptor, httpClient, converterFactory)
        .observe()
        .map(__ -> null);
  }
}
