package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetUserInfo;
import cm.aptoide.pt.dataprovider.model.v7.GetUserMeta;
import cm.aptoide.pt.dataprovider.model.v7.GetUserSettings;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateUserRequest;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.GetMySubscribedStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserMultipartRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserSettings;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AccountServiceV3 implements AccountService {

  private final AccountFactory accountFactory;
  private final OkHttpClient httpClient;
  private final OkHttpClient longTimeoutHttpClient;
  private final Converter.Factory converterFactory;
  private final ObjectMapper serializer;
  private final SharedPreferences sharedPreferences;
  private final String extraId;
  private final TokenInvalidator tokenInvalidator;
  private final AuthenticationPersistence authenticationPersistence;
  private final BodyInterceptor<BaseBody> v3NoAuthorizationBodyInterceptor;
  private final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptorV7;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorWebV7;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7;

  public AccountServiceV3(AccountFactory accountFactory, OkHttpClient httpClient,
      OkHttpClient longTimeoutHttpClient, Converter.Factory converterFactory,
      ObjectMapper serializer, SharedPreferences sharedPreferences, String extraId,
      TokenInvalidator refreshTokenInvalidator, AuthenticationPersistence authenticationPersistence,
      BodyInterceptor<BaseBody> v3NoAuthorizationBodyInterceptor,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptorV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorWebV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7) {
    this.accountFactory = accountFactory;
    this.httpClient = httpClient;
    this.longTimeoutHttpClient = longTimeoutHttpClient;
    this.converterFactory = converterFactory;
    this.serializer = serializer;
    this.sharedPreferences = sharedPreferences;
    this.extraId = extraId;
    this.tokenInvalidator = refreshTokenInvalidator;
    this.authenticationPersistence = authenticationPersistence;
    this.v3NoAuthorizationBodyInterceptor = v3NoAuthorizationBodyInterceptor;
    this.multipartBodyInterceptorV7 = multipartBodyInterceptorV7;
    this.bodyInterceptorWebV7 = bodyInterceptorWebV7;
    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
  }

  @Override public Single<Account> getAccount(String email, String password) {
    return createAccount(email.toLowerCase(), password, null,
        AptoideAccountManager.APTOIDE_SIGN_UP_TYPE);
  }

  @Override
  public Single<Account> createAccount(String email, String metadata, String name, String type) {
    return OAuth2AuthenticationRequest.of(email, metadata, type, null,
        v3NoAuthorizationBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, extraId)
        .observe()
        .toSingle()
        .flatMap(oAuth -> {
          if (!oAuth.hasErrors()) {
            return authenticationPersistence.createAuthentication(email, metadata,
                oAuth.getRefreshToken(), oAuth.getAccessToken(), type)
                .andThen(getAccount());
          } else {
            return Single.error(new AccountException(oAuth));
          }
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof AptoideWsV3Exception) {
            AptoideWsV3Exception exception = (AptoideWsV3Exception) throwable;
            return Single.error(new AccountException(exception));
          }
          return Single.error(throwable);
        });
  }

  @Override public Single<Account> createAccount(String email, String password) {
    return CreateUserRequest.of(email.toLowerCase(), password, v3NoAuthorizationBodyInterceptor,
        httpClient, tokenInvalidator, sharedPreferences, extraId)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response.hasErrors()) {
            return Single.error(new AccountException(response.getErrors()));
          }
          return getAccount(email, password);
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof AptoideWsV3Exception) {
            AptoideWsV3Exception exception = (AptoideWsV3Exception) throwable;
            return Single.error(new AccountException(exception));
          }
          return Single.error(throwable);
        });
  }

  @Override public Single<Account> getAccount() {
    return Single.zip(getServerAccount(), getSubscribedStores(),
        (response, stores) -> mapServerAccountToAccount(response, stores));
  }

  @Override public Completable updateAccount(String nickname, String avatarPath) {
    return SetUserMultipartRequest.of(nickname, avatarPath, multipartBodyInterceptorV7,
        longTimeoutHttpClient, converterFactory, serializer, tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new Exception(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable updateAccount(String accessLevel) {
    return SetUserRequest.of(accessLevel, bodyInterceptorPoolV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new Exception(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable updateAccountUsername(String username) {
    return SetUserRequest.ofWithName(username, bodyInterceptorPoolV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new Exception(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable unsubscribeStore(String storeName, String storeUserName,
      String storePassword) {
    return changeSubscription(storeName, storeUserName, storePassword,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.UNSUBSCRIBED);
  }

  @Override
  public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
    return changeSubscription(storeName, storeUserName, storePassword,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED);
  }

  @Override public Completable updateAccount(boolean adultContentEnabled) {
    return SetUserSettings.of(adultContentEnabled, httpClient, converterFactory,
        bodyInterceptorPoolV7, tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new Exception(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable removeAccount() {
    return authenticationPersistence.removeAuthentication();
  }

  /**
   * This retry occurs when user user is being propagated through the server slave machines
   * (specifically on user creation) so we use a retry with exponential back-off with three
   * retries to get the data.
   */
  private Observable<Throwable> retryOnTicket(Observable<? extends Throwable> observableError) {
    return observableError.zipWith(Observable.range(2, 4), (throwable, count) -> {
      try {
        AptoideWsV7Exception v7Exception = (AptoideWsV7Exception) throwable;
        List<BaseV7Response.Error> errors = v7Exception.getBaseResponse()
            .getErrors();
        if (errors != null && !errors.isEmpty() && errors.get(0)
            .getCode()
            .equalsIgnoreCase("user-1")) {
          return Observable.timer((long) Math.pow(5, count), TimeUnit.SECONDS).<Throwable>map(
              __ -> null);
        }
      } catch (ClassCastException | NullPointerException ex) {
        // does nothing
      }
      return Observable.<Throwable>error(throwable);
    })
        .flatMap(observable -> observable);
  }

  private Account mapServerAccountToAccount(GetUserInfo userInfo, List<Store> subscribedStores) {
    final GetUserMeta.Data userData = userInfo.getNodes()
        .getMeta()
        .getData();
    final GetUserSettings.Data userSettings = userInfo.getNodes()
        .getSettings()
        .getData();
    return accountFactory.createAccount(userData.getAccess(), subscribedStores,
        String.valueOf(userData.getId()), userData.getIdentity()
            .getEmail(), userData.getName(), userData.getAvatar(), mapToStore(userData.getStore()),
        userSettings.isMature(), userSettings.getAccess()
            .isConfirmed());
  }

  private Completable changeSubscription(String storeName, String storeUserName,
      String storePassword, ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription) {
    return ChangeStoreSubscriptionRequest.of(storeName, subscription, storeUserName, storePassword,
        bodyInterceptorPoolV7, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .toCompletable();
  }

  private Single<List<Store>> getSubscribedStores() {
    return new GetMySubscribedStoresRequest(bodyInterceptorPoolV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences).observe()
        .map(getUserRepoSubscription -> getUserRepoSubscription.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(store -> mapToStore(store))
        .toList()
        .toSingle();
  }

  private Store mapToStore(cm.aptoide.pt.dataprovider.model.v7.store.Store store) {
    if (store == null) {
      return Store.emptyStore();
    }
    final String publicAccessConstant =
        cm.aptoide.pt.dataprovider.model.v7.store.Store.PUBLIC_ACCESS;
    return new Store(store.getStats() == null ? 0 : store.getStats()
        .getDownloads(), store.getAvatar(), store.getId(), store.getName(),
        store.getAppearance() == null ? "DEFAULT" : store.getAppearance()
            .getTheme(), null, null, publicAccessConstant.equalsIgnoreCase(store.getAccess()));
  }

  private Single<GetUserInfo> getServerAccount() {
    return GetUserInfoRequest.of(httpClient, converterFactory, bodyInterceptorWebV7,
        tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            return Single.just(response);
          } else {
            return Single.error(new Exception(V7.getErrorMessage(response)));
          }
        })
        .retryWhen(observableError -> retryOnTicket(observableError).doOnNext(__ -> {
          Logger.w("AccountManagerService", "retryOnTicket() doOnNext()");
        }));
  }
}
