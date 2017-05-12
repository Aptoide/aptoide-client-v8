package cm.aptoide.pt.v8engine.timeline;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccount;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.view.timeline.ShareCardCallback;
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

  public SocialRepository(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor, Converter.Factory converterFactory,
      OkHttpClient httpClient) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
  }

  public void share(String cardId, boolean privacy, ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(cardId, bodyInterceptor, httpClient, converterFactory)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return accountManager.updateAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String cardId, ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(cardId, bodyInterceptor, httpClient, converterFactory)
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
      ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(cardId, storeId, httpClient, converterFactory, bodyInterceptor)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData()
                  .getCardUid());
            }
            return accountManager.updateAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String cardId, long storeId, ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(cardId, storeId, httpClient, converterFactory, bodyInterceptor)
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

  public void like(String timelineCardId, String cardType, String ownerHash, int rating) {
    LikeCardRequest.of(timelineCardId, cardType, ownerHash, rating, bodyInterceptor, httpClient,
        converterFactory)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> Logger.d(this.getClass()
            .getSimpleName(), baseV7Response.toString()), throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, Long storeId, String shareType, boolean privacy) {
    ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return accountManager.updateAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, Long storeId, String shareType) {
    ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory)
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

  private AptoideAccount.Access getAccountAccess(boolean privateAccess) {
    return privateAccess ? Account.Access.PRIVATE : Account.Access.PUBLIC;
  }
}

