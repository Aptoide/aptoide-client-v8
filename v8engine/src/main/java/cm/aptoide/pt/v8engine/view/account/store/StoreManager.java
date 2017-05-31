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

public class StoreManager {

  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;

  StoreManager(AptoideAccountManager accountManager, OkHttpClient httpClient,
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
    return Completable.defer(() -> {
      if (storeExists) {
        return updateStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
            storeThemeName);
      }
      return createStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
          storeThemeName);
    });
  }

  /**
   * To create a store we need to call WS CheckUserCredentials so we can associate a
   * user to a newly created store.
   *
   * Then, if we have more data we either use a SetStore with multi-part request if we have
   * a store image, or a SetStore without image. This is the edit store use case {@link
   * #updateStore}.
   */
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

  /**
   * If we have more data we either use a SetStore with multi-part request if we have
   * a store image, or a SetStore without image.
   */
  private Completable updateStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {
    return Completable.defer(() -> {
      if (hasNewAvatar) {
        return updateStoreWithAvatar(storeId, storeName, storeDescription, storeImage,
            storeThemeName);
      }
      return updateStoreWithoutAvatar(storeId, storeDescription, storeThemeName);
    });
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
