package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {
  public SocialRepository() {

  }

  public void share(TimelineCard timelineCard, String cardType, String ownerHash) {
    if (timelineCard instanceof Article) {
      ShareCardRequest.ofArticle((Article) timelineCard, cardType, ownerHash,
          AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(),
          AptoideAccountManager.getUserEmail())
          .observe()
          .observeOn(Schedulers.io())
          .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
              baseV7Response.toString()));
    }
  }

  public void like() {

  }
}

