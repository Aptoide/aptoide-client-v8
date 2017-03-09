package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor bodyInterceptor;

  public SocialRepository(AptoideAccountManager accountManager, BodyInterceptor bodyInterceptor) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
  }

  public void share(TimelineCard timelineCard, Context context, boolean privacy) {
    ShareCardRequest.of(timelineCard, accountManager.getAccessToken(), bodyInterceptor)
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

  public void like(TimelineCard timelineCard, String cardType, String ownerHash, int rating) {
    LikeCardRequest.of(timelineCard, cardType, ownerHash, rating, bodyInterceptor,
        accountManager.getAccessToken())
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(
            baseV7Response -> Logger.d(this.getClass().getSimpleName(), baseV7Response.toString()),
            throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, String shareType, boolean privacy) {
    ShareInstallCardRequest.of(packageName, accountManager.getAccessToken(), shareType,
        bodyInterceptor).observe().toSingle().flatMapCompletable(response -> {
      if (response.isOk()) {
        return accountManager.updateAccount(getAccountAccess(privacy));
      }
      return Completable.error(
          new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
    }).subscribe(() -> {
    }, throwable -> throwable.printStackTrace());
  }

  private Account.Access getAccountAccess(boolean privacy) {
    return privacy ? Account.Access.UNLISTED : Account.Access.PUBLIC;
  }
}

