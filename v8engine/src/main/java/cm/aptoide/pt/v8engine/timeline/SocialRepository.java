package cm.aptoide.pt.v8engine.timeline;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccount;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TimelineAnalytics timelineAnalytics;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public SocialRepository(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor, Converter.Factory converterFactory,
      OkHttpClient httpClient, TimelineAnalytics timelineAnalytics,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.timelineAnalytics = timelineAnalytics;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public void share(String cardId, boolean privacy, ShareCardCallback shareCardCallback,
      TimelineSocialActionData timelineSocialActionData) {
    //todo(pribeiro): check if timelineSocialActionData is null
    ShareCardRequest.of(cardId, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return accountManager.syncCurrentAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
          timelineAnalytics.sendSocialActionEvent(timelineSocialActionData);
        }, throwable -> throwable.printStackTrace());
  }

  private AptoideAccount.Access getAccountAccess(boolean privateAccess) {
    return privateAccess ? Account.Access.PRIVATE : Account.Access.PUBLIC;
  }

  public void share(String cardId, ShareCardCallback shareCardCallback,
      TimelineSocialActionData timelineSocialActionData) {
    ShareCardRequest.of(cardId, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return Completable.complete();
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String cardId, long storeId, boolean privacy,
      ShareCardCallback shareCardCallback, TimelineSocialActionData timelineSocialActionData) {
    ShareCardRequest.of(cardId, storeId, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return accountManager.syncCurrentAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String cardId, long storeId, ShareCardCallback shareCardCallback,
      TimelineSocialActionData timelineSocialActionData) {
    ShareCardRequest.of(cardId, storeId, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return Completable.complete();
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
          timelineAnalytics.sendSocialActionEvent(timelineSocialActionData);
        }, throwable -> throwable.printStackTrace());
  }

  public void like(String timelineCardId, String cardType, String ownerHash, int rating,
      TimelineSocialActionData timelineSocialActionData) {
    LikeCardRequest.of(timelineCardId, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> {
          Logger.d(this.getClass()
              .getSimpleName(), baseV7Response.toString());
          timelineAnalytics.sendSocialActionEvent(timelineSocialActionData);
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, Long storeId, String shareType, boolean privacy) {
    ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return accountManager.syncCurrentAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, Long storeId, String shareType) {
    //todo(pribeiro): check if timelineSocialActionData is null
    ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }
}

