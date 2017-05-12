package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.ws.v3.ChangeUserSettingsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreateUserRequest;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.GetMySubscribedStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.model.v3.OAuth;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class AccountManagerService {

  private final BasebBodyInterceptorFactory interceptorFactory;
  private final AccountFactory accountFactory;
  private final OkHttpClient httpClient;
  private final OkHttpClient longTimeoutHttpClient;
  private final Converter.Factory converterFactory;

  public AccountManagerService(BasebBodyInterceptorFactory interceptorFactory,
      AccountFactory accountFactory, OkHttpClient httpClient, OkHttpClient longTimeoutHttpClient,
      Converter.Factory converterFactory) {
    this.interceptorFactory = interceptorFactory;
    this.accountFactory = accountFactory;
    this.httpClient = httpClient;
    this.longTimeoutHttpClient = longTimeoutHttpClient;
    this.converterFactory = converterFactory;
  }

  public Completable createAccount(String email, String password) {
    return CreateUserRequest.of(email.toLowerCase(), password, interceptorFactory.createV3(),
        httpClient)
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

  public Single<OAuth> login(String type, String email, String password, String name) {
    return OAuth2AuthenticationRequest.of(email, password, type, name,
        interceptorFactory.createV3(), httpClient, converterFactory)
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

  public Completable updateAccount(String email, String nickname, String password,
      String avatarPath, String accessToken) {
    return CreateUserRequest.of(email, nickname, password, avatarPath, accessToken,
        interceptorFactory.createV3(), httpClient, longTimeoutHttpClient)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (!response.hasErrors()) {
            return Completable.complete();
          } else {
            return Completable.error(new AccountException(response.getErrors()));
          }
        });
  }

  public Completable updateAccount(String accessLevel, AptoideAccountManager accountManager) {
    return SetUserRequest.of(accessLevel, interceptorFactory.createV7(accountManager), httpClient,
        converterFactory)
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
        interceptorFactory.createV7(accountManager), httpClient, converterFactory)
        .observe()
        .toSingle()
        .toCompletable();
  }

  private Single<List<Store>> getSubscribedStores(String accessToken,
      AptoideAccountManager accountManager) {
    return new GetMySubscribedStoresRequest(accessToken,
        interceptorFactory.createV7(accountManager), httpClient, converterFactory).observe()
        .map(getUserRepoSubscription -> getUserRepoSubscription.getDatalist()
            .getList())
        .flatMapIterable(list -> list)
        .map(store -> mapToStore(store))
        .toList()
        .toSingle();
  }

  private Store mapToStore(cm.aptoide.pt.model.v7.store.Store store) {
    return new Store(store.getStats()
        .getDownloads(), store.getAvatar(), store.getId(), store.getName(), store.getAppearance()
        .getTheme(), null, null);
  }

  public Single<Account> getAccount(String accessToken, String refreshToken,
      String encryptedPassword, String type, AptoideAccountManager accountManager) {
    return Single.zip(getServerAccount(accessToken),
        getSubscribedStores(accessToken, accountManager),
        (response, stores) -> mapServerAccountToAccount(response, refreshToken, accessToken,
            encryptedPassword, type, stores));
  }

  private Single<CheckUserCredentialsJson> getServerAccount(String accessToken) {
    return CheckUserCredentialsRequest.of(accessToken, interceptorFactory.createV3(), httpClient,
        converterFactory)
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response.getStatus()
              .equals("OK")) {
            return Single.just(response);
          }
          return Single.error(new IllegalStateException("Failed to get user account"));
        });
  }

  private Account mapServerAccountToAccount(CheckUserCredentialsJson serverUser,
      String refreshToken, String accessToken, String encryptedPassword, String type,
      List<Store> subscribedStores) {
    return accountFactory.createAccount(serverUser.getAccess(), subscribedStores,
        String.valueOf(serverUser.getId()), serverUser.getEmail(), serverUser.getUsername(),
        serverUser.getAvatar(), refreshToken, accessToken, encryptedPassword,
        Account.Type.valueOf(type), serverUser.getRepo(), serverUser.getRavatarHd(),
        serverUser.getSettings()
            .getMatureswitch()
            .equals("active"), serverUser.isAccessConfirmed());
  }

  public Completable updateAccount(boolean adultContentEnabled, String accessToken) {
    return ChangeUserSettingsRequest.of(adultContentEnabled, accessToken,
        interceptorFactory.createV3(), httpClient, converterFactory)
        .observe(true)
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.getStatus()
              .equals("OK")) {
            return Completable.complete();
          } else {
            return Completable.error(new Exception(V3.getErrorMessage(response)));
          }
        });
  }
}