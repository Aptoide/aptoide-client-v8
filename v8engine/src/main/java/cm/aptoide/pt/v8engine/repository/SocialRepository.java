package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
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
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;

  public SocialRepository(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
  }

  public void share(TimelineCard timelineCard, Context context, boolean privacy,
      ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(timelineCard, bodyInterceptor)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData().getCardUid());
            }
            return accountManager.updateAccount(getAccountAccess(privacy));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void share(TimelineCard timelineCard, Context context,
      ShareCardCallback shareCardCallback) {
    ShareCardRequest.of(timelineCard, bodyInterceptor)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            if (shareCardCallback != null) {
              shareCardCallback.onCardShared(response.getData().getCardUid());
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
    LikeCardRequest.of(timelineCardId, cardType, ownerHash, rating, bodyInterceptor)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(
            baseV7Response -> Logger.d(this.getClass().getSimpleName(), baseV7Response.toString()),
            throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, String shareType, boolean privacy) {
    ShareInstallCardRequest.of(packageName, shareType, bodyInterceptor)
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

  public void share(String packageName, String shareType) {
    ShareInstallCardRequest.of(packageName, shareType, bodyInterceptor)
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

