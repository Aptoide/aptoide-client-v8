package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
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
import cm.aptoide.pt.model.v3.OAuth;
import cm.aptoide.pt.model.v7.GetUserInfo;
import cm.aptoide.pt.model.v7.GetUserMeta;
import cm.aptoide.pt.model.v7.GetUserSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class AccountManagerService {

  private final AccountManagerInterceptorFactory interceptorFactory;
  private final AccountFactory accountFactory;
  private final OkHttpClient httpClient;
  private final OkHttpClient longTimeoutHttpClient;
  private final Converter.Factory converterFactory;
  private final ObjectMapper serializer;
  private final TokenInvalidator tokenInvalidator;

  public AccountManagerService(AccountManagerInterceptorFactory interceptorFactory,
      AccountFactory accountFactory, OkHttpClient httpClient, OkHttpClient longTimeoutHttpClient,
      Converter.Factory converterFactory, ObjectMapper serializer,
      TokenInvalidator tokenInvalidator) {
    this.interceptorFactory = interceptorFactory;
    this.accountFactory = accountFactory;
    this.httpClient = httpClient;
    this.longTimeoutHttpClient = longTimeoutHttpClient;
    this.converterFactory = converterFactory;
    this.serializer = serializer;
    this.tokenInvalidator = tokenInvalidator;
  }

  public Completable createAccount(String email, String password,
      AptoideAccountManager accountManager) {
    return CreateUserRequest.of(email.toLowerCase(), password,
        interceptorFactory.createV3(accountManager), httpClient)
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
        interceptorFactory.createV3(accountManager), httpClient, converterFactory)
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
        converterFactory, serializer, tokenInvalidator)
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
        converterFactory, tokenInvalidator)
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
        httpClient, converterFactory, tokenInvalidator)
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
        interceptorFactory.createV7(accountManager), httpClient, converterFactory, tokenInvalidator)
        .observe()
        .toSingle()
        .toCompletable();
  }

  private Single<List<Store>> getSubscribedStores(String accessToken,
      AptoideAccountManager accountManager) {
    return new GetMySubscribedStoresRequest(accessToken,
        interceptorFactory.createV7(accountManager), httpClient, converterFactory, tokenInvalidator).observe()
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
    return Single.zip(getServerAccount(accountManager, accessToken),
        getSubscribedStores(accessToken, accountManager),
        (response, stores) -> mapServerAccountToAccount(response, refreshToken, accessToken,
            encryptedPassword, type, stores));
  }

  private Single<GetUserInfo> getServerAccount(AptoideAccountManager accountManager,
      String accessToken) {
    return GetUserInfoRequest.of(accessToken, httpClient, converterFactory,
        interceptorFactory.createUserInfoV7(accountManager), tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            return Single.just(response);
          } else {
            return Single.error(new Exception(V7.getErrorMessage(response)));
          }
        });
  }

  private Account mapServerAccountToAccount(GetUserInfo userInfo, String refreshToken,
      String accessToken, String encryptedPassword, String type, List<Store> subscribedStores) {
    final GetUserMeta.Data userData = userInfo.getNodes()
        .getMeta()
        .getData();
    final GetUserSettings.Data userSettings = userInfo.getNodes()
        .getSettings()
        .getData();
    final String storeName = userData.getStore() == null ? "" : userData.getStore()
        .getName();
    final String storeAvatar = userData.getStore() == null ? "" : userData.getStore()
        .getAvatar();
    return accountFactory.createAccount(userData.getAccess(), subscribedStores,
        String.valueOf(userData.getId()), userData.getIdentity()
            .getEmail(), userData.getName(), userData.getAvatar(), refreshToken, accessToken,
        encryptedPassword, Account.Type.valueOf(type), storeName, storeAvatar,
        userSettings.isMature(), userSettings.getAccess()
            .isConfirmed());
  }

  public Completable updateAccount(boolean adultContentEnabled,
      AptoideAccountManager accountManager) {
    return SetUserSettings.of(adultContentEnabled, httpClient, converterFactory,
        interceptorFactory.createAdultContentV7(accountManager, adultContentEnabled),
        tokenInvalidator)
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
