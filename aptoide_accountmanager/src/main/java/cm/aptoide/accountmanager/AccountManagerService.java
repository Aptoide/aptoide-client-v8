package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.ws.v3.CreateUserRequest;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v3.OAuth;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class AccountManagerService {

  private final AptoideClientUUID aptoideClientUUID;
  private final BodyInterceptorFactory interceptorFactory;

  public AccountManagerService(AptoideClientUUID aptoideClientUUID,
      BodyInterceptorFactory interceptorFactory) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.interceptorFactory = interceptorFactory;
  }

  public Single<String> refreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUUID.getUniqueIdentifier())
        .observe()
        .subscribeOn(Schedulers.io())
        .toSingle()
        .flatMap(oAuth -> {
          if (!oAuth.hasErrors()) {
            return Single.just(oAuth.getAccessToken());
          } else {
            return Single.error(new AccountException(oAuth.getError()));
          }
        });
  }

  public Completable createAccount(String email, String password) {
    return CreateUserRequest.of(email.toLowerCase(), password,
        aptoideClientUUID.getUniqueIdentifier())
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
                ((AptoideWsV3Exception) throwable).getBaseResponse().getError()));
          }
          return Completable.error(throwable);
        });
  }

  public Single<OAuth> login(String type, String email, String password, String name) {
    return OAuth2AuthenticationRequest.of(email, password, type, name,
        aptoideClientUUID.getUniqueIdentifier()).observe().toSingle().flatMap(oAuth -> {
      if (!oAuth.hasErrors()) {
        return Single.just(oAuth);
      } else {
        return Single.error(new AccountException(oAuth.getError()));
      }
    }).onErrorResumeNext(throwable -> {
      if (throwable instanceof AptoideWsV3Exception) {
        return Single.error(
            new AccountException(((AptoideWsV3Exception) throwable).getBaseResponse().getError()));
      }
      return Single.error(throwable);
    });
  }

  public Completable updateAccount(String email, String nickname, String password,
      String avatarPath, String accessToken) {
    return CreateUserRequest.of(email, nickname, password, avatarPath,
        aptoideClientUUID.getUniqueIdentifier(), accessToken)
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
    return SetUserRequest.of(accessLevel, interceptorFactory.create(accountManager))
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
        interceptorFactory.create(accountManager)).observe().toSingle().toCompletable();
  }
}