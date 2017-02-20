package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.activity.AccountBaseActivity;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;

  public SocialRepository(AptoideAccountManager accountManager, AptoideClientUUID aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
  }

  public void share(TimelineCard timelineCard, Context context, boolean privacy) {
    String accessToken = accountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getUniqueIdentifier();
    ShareCardRequest.of(timelineCard, accessToken, aptoideClientUUID)
        .observe()
        .subscribe(baseV7Response -> {
          final String userAccess = privacy ? AccountBaseActivity.UserAccessState.UNLISTED.toString()
              : AccountBaseActivity.UserAccessState.PUBLIC.toString();
          SetUserRequest.of(aptoideClientUUID, userAccess, accessToken)
              .observe()
              .subscribe(baseV7Response1 -> Logger.d(this.getClass().getSimpleName(),
                  baseV7Response.toString()), throwable -> throwable.printStackTrace());
          ManagerPreferences.setUserAccess(userAccess);
          ManagerPreferences.setUserAccessConfirmed(true);
        }, throwable -> throwable.printStackTrace());
  }

  public void like(TimelineCard timelineCard, String cardType, String ownerHash, int rating) {
    String accessToken = accountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getUniqueIdentifier();
    String email = accountManager.getUserEmail();
    LikeCardRequest.of(timelineCard, cardType, ownerHash, accessToken, aptoideClientUUID, rating)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(
            baseV7Response -> Logger.d(this.getClass().getSimpleName(), baseV7Response.toString()),
            throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, String shareType, boolean privacy) {
    String accessToken = accountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getUniqueIdentifier();
    ShareInstallCardRequest.of(packageName, accessToken, shareType, aptoideClientUUID)
        .observe()
        .subscribe(baseV7Response -> {
          final String userAccess = privacy ? AccountBaseActivity.UserAccessState.UNLISTED.toString()
              : AccountBaseActivity.UserAccessState.PUBLIC.toString();
          SetUserRequest.of(aptoideClientUUID, userAccess, accessToken)
              .observe()
              .subscribe(baseV7Response1 -> Logger.d(this.getClass().getSimpleName(),
                  baseV7Response.toString()), throwable -> throwable.printStackTrace());
          ManagerPreferences.setUserAccess(userAccess);
          ManagerPreferences.setUserAccessConfirmed(true);
          Logger.d(this.getClass().getSimpleName(), baseV7Response.toString());
        }, throwable -> throwable.printStackTrace());
  }
}

