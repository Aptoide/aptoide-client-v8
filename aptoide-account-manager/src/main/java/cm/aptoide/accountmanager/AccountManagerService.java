package cm.aptoide.accountmanager;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.model.v3.OAuth;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetUserInfo;
import cm.aptoide.pt.dataprovider.model.v7.GetUserMeta;
import cm.aptoide.pt.dataprovider.model.v7.GetUserSettings;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AccountManagerService {

  private final AccountManagerInterceptorFactory interceptorFactory;
  private final AccountFactory accountFactory;
  private final OkHttpClient httpClient;
  private final OkHttpClient longTimeoutHttpClient;
  private final Converter.Factory converterFactory;
  private final ObjectMapper serializer;
  private final AccountManagerTokenInvalidatorFactory tokenInvalidatorFactory;
  private final SharedPreferences sharedPreferences;

  public AccountManagerService(AccountManagerInterceptorFactory interceptorFactory,
      AccountFactory accountFactory, OkHttpClient httpClient, OkHttpClient longTimeoutHttpClient,
      Converter.Factory converterFactory, ObjectMapper serializer,
      AccountManagerTokenInvalidatorFactory tokenInvalidatorFactory,
      SharedPreferences sharedPreferences) {
    this.interceptorFactory = interceptorFactory;
    this.accountFactory = accountFactory;
    this.httpClient = httpClient;
    this.longTimeoutHttpClient = longTimeoutHttpClient;
    this.converterFactory = converterFactory;
    this.serializer = serializer;
    this.tokenInvalidatorFactory = tokenInvalidatorFactory;
    this.sharedPreferences = sharedPreferences;
  }

  public Completable createAccount(String email, String password,
      AptoideAccountManager accountManager) {
    return CreateUserRequest.of(email.toLowerCase(), password,
        interceptorFactory.createV3(accountManager), httpClient,
        tokenInvalidatorFactory.getTokenInvalidator(accountManager), sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.hasErrors()) {
            return Completable.error(new AccountException(response.getErrors()));
          }
          return Completable.complete();
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof AptoideWsV3Exception) {
            return Completable.error(new AccountException(
                ((AptoideWsV3Exception) throwable).getBaseResponse()
                    .getError()));
          }
          return Completable.error(throwable);
        });
  }

  public Single<OAuth> login(String type, String email, String password, String name,
      AptoideAccountManager accountManager) {
    return OAuth2AuthenticationRequest.of(email, password, type, name,
        interceptorFactory.createV3(accountManager), httpClient, converterFactory,
        tokenInvalidatorFactory.getTokenInvalidator(accountManager), sharedPreferences)
        .observe()
        .toSingle()
        .flatMap(oAuth -> {
          if (!oAuth.hasErrors()) {
            return Single.just(oAuth);
          } else {
            return Single.error(new AccountException(oAuth.getError()));
          }
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof AptoideWsV3Exception) {
            return Single.error(new AccountException(
                ((AptoideWsV3Exception) throwable).getBaseResponse()
                    .getError()));
          }
          return Single.error(throwable);
        });
  }

  public Completable updateAccount(String nickname, String avatarPath,
      AptoideAccountManager accountManager) {
    return SetUserMultipartRequest.of(nickname, avatarPath,
        interceptorFactory.createMultipartBodyInterceptor(accountManager), longTimeoutHttpClient,
        converterFactory, serializer, tokenInvalidatorFactory.getTokenInvalidator(accountManager))
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

  public Completable updateAccount(String accessLevel, AptoideAccountManager accountManager) {
    return SetUserRequest.of(accessLevel, interceptorFactory.createV7(accountManager), httpClient,
        converterFactory, tokenInvalidatorFactory.getTokenInvalidator(accountManager),
        sharedPreferences)
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

  public Completable updateAccountUsername(String username, AptoideAccountManager accountManager) {
    return SetUserRequest.ofWithName(username, interceptorFactory.createV7(accountManager),
        httpClient, converterFactory, tokenInvalidatorFactory.getTokenInvalidator(accountManager),
        sharedPreferences)
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

  public Completable unsubscribeStore(String storeName, String storeUserName, String storePassword,
      AptoideAccountManager accountManager) {
    return changeSubscription(storeName, storeUserName, storePassword, accountManager,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.UNSUBSCRIBED);
  }

  public Completable subscribeStore(String storeName, String storeUserName, String storePassword,
      AptoideAccountManager accountManager) {
    return changeSubscription(storeName, storeUserName, storePassword, accountManager,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED);
  }

  private Completable changeSubscription(String storeName, String storeUserName,
      String storePassword, AptoideAccountManager accountManager,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription) {
    return ChangeStoreSubscriptionRequest.of(storeName, subscription, storeUserName, storePassword,
        interceptorFactory.createV7(accountManager), httpClient, converterFactory,
        tokenInvalidatorFactory.getTokenInvalidator(accountManager), sharedPreferences)
        .observe()
        .toSingle()
        .toCompletable();
  }

  private Single<List<Store>> getSubscribedStores(String accessToken,
      AptoideAccountManager accountManager) {
    return new GetMySubscribedStoresRequest(accessToken,
        interceptorFactory.createV7(accountManager), httpClient, converterFactory,
        tokenInvalidatorFactory.getTokenInvalidator(accountManager), sharedPreferences).observe()
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

  public Single<Account> getAccount(String accessToken, String refreshToken,
      String encryptedPassword, String type, AptoideAccountManager accountManager) {
    return Single.zip(getServerAccount(accountManager, accessToken),
        getSubscribedStores(accessToken, accountManager),
        (response, stores) -> mapServerAccountToAccount(response, refreshToken, accessToken,
            encryptedPassword, type, stores));
  }

  private Single<GetUserInfo> getServerAccount(AptoideAccountManager accountManager,
      String accessToken) {
    return GetUserInfoRequest.of(accessToken, httpClient, converterFactory,
        interceptorFactory.createUserInfoV7(accountManager),
        tokenInvalidatorFactory.getTokenInvalidator(accountManager))
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

  /**
   * This retry occurs when user user is being propagated through the server slave machines
   * (specifically on user creation) so we use a retry with exponential back-off with three
   * retries to get the data.
   */
  @NonNull private Observable<Throwable> retryOnTicket(
      Observable<? extends Throwable> observableError) {
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

  private Account mapServerAccountToAccount(GetUserInfo userInfo, String refreshToken,
      String accessToken, String encryptedPassword, String type, List<Store> subscribedStores) {
    final GetUserMeta.Data userData = userInfo.getNodes()
        .getMeta()
        .getData();
    final GetUserSettings.Data userSettings = userInfo.getNodes()
        .getSettings()
        .getData();
    return accountFactory.createAccount(userData.getAccess(), subscribedStores,
        String.valueOf(userData.getId()), userData.getIdentity()
            .getEmail(), userData.getName(), userData.getAvatar(), refreshToken, accessToken,
        encryptedPassword, Account.Type.valueOf(type), mapToStore(userData.getStore()),
        userSettings.isMature(), userSettings.getAccess()
            .isConfirmed());
  }

  public Completable updateAccount(boolean adultContentEnabled,
      AptoideAccountManager accountManager) {
    return SetUserSettings.of(adultContentEnabled, httpClient, converterFactory,
        interceptorFactory.createAdultContentV7(accountManager, adultContentEnabled),
        tokenInvalidatorFactory.getTokenInvalidator(accountManager))
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
}
