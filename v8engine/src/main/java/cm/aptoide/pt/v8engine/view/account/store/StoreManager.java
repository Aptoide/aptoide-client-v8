package cm.aptoide.pt.v8engine.view.account.store;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

class StoreManager {

  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;

  public StoreManager(AptoideAccountManager accountManager, OkHttpClient httpClient,
      Converter.Factory converterFactory, StoreBodyInterceptor bodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3) {

    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  public Completable createOrUpdate(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName, boolean storeExists) {
    return Completable.fromCallable(() -> {
      if (storeExists) {
        return updateStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
            storeThemeName);
      }
      return createStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
          storeThemeName);
    });
  }

  private Completable createStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> CheckUserCredentialsRequest.of(storeName, account.getAccessToken(),
            bodyInterceptorV3, httpClient, converterFactory)
            .observe()
            .toSingle())
        .flatMapCompletable(__ -> {
          if (needToUploadMoreStoreData(storeDescription, storeImage, hasNewAvatar)) {
            return updateStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
                storeThemeName);
          }
          return Completable.complete();
        });
  }

  private boolean needToUploadMoreStoreData(String storeDescription, String storeImage,
      boolean hasNewAvatar) {
    return !TextUtils.isEmpty(storeDescription) || (hasNewAvatar && !TextUtils.isEmpty(storeImage));
  }

  private Completable updateStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {
    if (hasNewAvatar) {
      return updateStoreWithAvatar(storeId, storeName, storeDescription, storeImage,
          storeThemeName);
    }
    return updateStoreWithoutAvatar(storeId, storeDescription, storeThemeName);
  }

  private Completable updateStoreWithoutAvatar(long storeId, String storeDescription,
      String storeThemeName) {
    return SimpleSetStoreRequest.of(storeId, storeThemeName, storeDescription, bodyInterceptor,
        httpClient, converterFactory)
        .observe()
        .toCompletable();
  }

  private Completable updateStoreWithAvatar(long storeId, String storeName, String storeDescription,
      String storeImage, String storeThemeName) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> SetStoreRequest.of(account.getAccessToken(), storeName, storeThemeName,
            storeImage, storeDescription, true, storeId, bodyInterceptor, httpClient,
            converterFactory)
            .observe()
            .toSingle())
        .toCompletable();
  }
}
