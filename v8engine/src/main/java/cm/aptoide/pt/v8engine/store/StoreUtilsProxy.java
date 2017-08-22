package cm.aptoide.pt.v8engine.store;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

/**
 * This Proxy class was created to solve the issue with calling Analytics tracking events inside
 * StoreUtils, which is in the dataprovider module. If you want
 * an event to be tracked (User subscribes a store) use this class's subscribeStore method. Else,
 * just keep doing whatever you are doing.
 *
 * Created by jdandrade on 02/08/16.
 */
public class StoreUtilsProxy {

  private final StoreAccessor storeAccessor;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final SharedPreferences sharedPreferences;
  private TokenInvalidator tokenInvalidator;

  public StoreUtilsProxy(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor, StoreCredentialsProvider storeCredentialsProvider,
      StoreAccessor storeAccessor, OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.storeAccessor = storeAccessor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public void subscribeStore(String storeName) {
    subscribeStore(
        GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
            bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences),
        null, null, storeName, accountManager);
  }

  public Observable<GetStoreMeta> subscribeStoreObservable(String storeName) {
    return StoreUtils.subscribeStore(
        GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
            bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences),
        accountManager, null, null, storeAccessor);
  }

  public void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, String storeName,
      AptoideAccountManager accountManager) {

    subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener, storeName,
        accountManager, null, null);
  }

  public void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, String storeName,
      AptoideAccountManager accountManager, String storeUserName, String storePassword) {

    StoreUtils.subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener,
        accountManager, storeUserName, storePassword, storeAccessor);
  }

  public void subscribeStore(String storeName,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, AptoideAccountManager accountManager) {

    subscribeStore(
        GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
            bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences),
        successRequestListener, errorRequestListener, storeName, accountManager);
  }

  public void unSubscribeStore(String storeName,
      StoreCredentialsProvider storeCredentialsProvider) {
    StoreUtils.unSubscribeStore(storeName, accountManager, storeCredentialsProvider, storeAccessor);
  }

  public Completable addDefaultStore(GetStoreMetaRequest getStoreMetaRequest,
      AptoideAccountManager accountManager,
      BaseRequestWithStore.StoreCredentials storeCredentials) {

    return getStoreMetaRequest.observe()
        .flatMap(getStoreMeta -> {
          if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo()
              .getStatus())) {
            if (accountManager.isLoggedIn()) {
              return accountManager.subscribeStore(getStoreMeta.getData()
                  .getName(), storeCredentials.getUsername(), storeCredentials.getPasswordSha1())
                  .andThen(Observable.just(getStoreMeta));
            } else {
              return Observable.just(getStoreMeta);
            }
          } else {
            return Observable.error(new Exception("Something went wrong while getting store meta"));
          }
        })
        .doOnNext(
            getStoreMeta -> saveStore(getStoreMeta.getData(), getStoreMetaRequest, storeAccessor))
        .doOnError((throwable) -> CrashReport.getInstance()
            .log(throwable))
        .toCompletable();
  }

  private void saveStore(cm.aptoide.pt.dataprovider.model.v7.store.Store storeData,
      GetStoreMetaRequest getStoreMetaRequest, StoreAccessor storeAccessor) {
    Store store = new Store();

    store.setStoreId(storeData.getId());
    store.setStoreName(storeData.getName());
    store.setDownloads(storeData.getStats()
        .getDownloads());

    store.setIconPath(storeData.getAvatar());
    store.setTheme(storeData.getAppearance()
        .getTheme());

    if (isPrivateCredentialsSet(getStoreMetaRequest)) {
      store.setUsername(getStoreMetaRequest.getBody()
          .getStoreUser());
      store.setPasswordSha1(getStoreMetaRequest.getBody()
          .getStorePassSha1());
    }
    storeAccessor.save(store);
  }

  private boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
    return getStoreMetaRequest.getBody()
        .getStoreUser() != null
        && getStoreMetaRequest.getBody()
        .getStorePassSha1() != null;
  }
}
