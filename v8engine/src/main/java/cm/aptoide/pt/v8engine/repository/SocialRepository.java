package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {
  public SocialRepository() {

  }

  public void share(TimelineCard timelineCard, String cardType, String ownerHash) {
    String accessToken = AptoideAccountManager.getAccessToken();
    String aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();
    String email = AptoideAccountManager.getUserEmail();
    if (timelineCard instanceof Article) {
      ShareCardRequest.ofArticle((Article) timelineCard, cardType, ownerHash, accessToken,
          aptoideClientUUID, email)
          .observe()
          .observeOn(Schedulers.io())
          .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
              baseV7Response.toString()), throwable -> throwable.printStackTrace());
    } else if (timelineCard instanceof Video) {
      ShareCardRequest.ofVideo((Video) timelineCard, cardType, ownerHash, accessToken,
          aptoideClientUUID, email)
          .observe()
          .observeOn(Schedulers.io())
          .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
              baseV7Response.toString()), throwable -> throwable.printStackTrace());
    } else if (timelineCard instanceof StoreLatestApps) {
      ShareCardRequest.ofStoreLatestApps((StoreLatestApps) timelineCard, cardType, ownerHash,
          accessToken, aptoideClientUUID, email)
          .observe()
          .observeOn(Schedulers.io())
          .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
              baseV7Response.toString()), throwable -> throwable.printStackTrace());
    }
  }

  public void like(TimelineCard timelineCard, String cardType, String ownerHash) {
    String accessToken = AptoideAccountManager.getAccessToken();
    String aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();
    String email = AptoideAccountManager.getUserEmail();
    if (timelineCard instanceof SocialArticle) {
      LikeCardRequest.of((SocialArticle) timelineCard, cardType, ownerHash, accessToken,
          aptoideClientUUID, email)
          .observe()
          .observeOn(Schedulers.io())
          .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
              baseV7Response.toString()), throwable -> throwable.printStackTrace());
    }
  }
}

