package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BaseActivity;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final AptoideClientUUID aptoideClientUUID;

  public SocialRepository() {

    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  public void share(TimelineCard timelineCard, Context context, boolean privacy) {
    String accessToken = AptoideAccountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getAptoideClientUUID();
    ShareCardRequest.of(timelineCard, accessToken, aptoideClientUUID)
        .observe()
        .subscribe(baseV7Response -> {
          final String userAccess = privacy ? BaseActivity.UserAccessState.UNLISTED.toString()
              : BaseActivity.UserAccessState.PUBLIC.toString();
          SetUserRequest.of(aptoideClientUUID, userAccess, accessToken, null)
              .observe()
              .subscribe(baseV7Response1 -> Logger.d(this.getClass().getSimpleName(),
                  baseV7Response.toString()), throwable -> throwable.printStackTrace());
          ManagerPreferences.setUserAccess(userAccess);
          ManagerPreferences.setUserAccessConfirmed(true);
        }, throwable -> throwable.printStackTrace());
  }

  public void like(TimelineCard timelineCard, String cardType, String ownerHash, int rating) {
    String accessToken = AptoideAccountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getAptoideClientUUID();
    String email = AptoideAccountManager.getUserEmail();
    LikeCardRequest.of(timelineCard, cardType, ownerHash, accessToken, aptoideClientUUID, rating)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(
            baseV7Response -> Logger.d(this.getClass().getSimpleName(), baseV7Response.toString()),
            throwable -> throwable.printStackTrace());
  }

  public void share(String packageName, String shareType, boolean privacy) {
    String accessToken = AptoideAccountManager.getAccessToken();
    String aptoideClientUUID = this.aptoideClientUUID.getAptoideClientUUID();
    ShareInstallCardRequest.of(packageName, accessToken, shareType, aptoideClientUUID)
        .observe()
        .subscribe(baseV7Response -> {
          final String userAccess = privacy ? BaseActivity.UserAccessState.UNLISTED.toString()
              : BaseActivity.UserAccessState.PUBLIC.toString();
          SetUserRequest.of(aptoideClientUUID, userAccess, accessToken, null)
              .observe()
              .subscribe(baseV7Response1 -> Logger.d(this.getClass().getSimpleName(),
                  baseV7Response.toString()), throwable -> throwable.printStackTrace());
          ManagerPreferences.setUserAccess(userAccess);
          ManagerPreferences.setUserAccessConfirmed(true);
          Logger.d(this.getClass().getSimpleName(), baseV7Response.toString());
        }, throwable -> throwable.printStackTrace());
  }
}

