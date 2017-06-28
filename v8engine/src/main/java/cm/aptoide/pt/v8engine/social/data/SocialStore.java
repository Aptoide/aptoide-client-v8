package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class SocialStore extends StoreLatestApps {
  private final Poster poster;

  public SocialStore(String cardId, Poster poster, String storeName, String storeAvatar,
      int subscribers, int appsNumber, Date latestUpdate, List<App> apps, String abUrl,
      CardType cardType) {
    super(cardId, storeName, storeAvatar, subscribers, appsNumber, latestUpdate, apps, abUrl,
        cardType);
    this.poster = poster;
  }

  public Poster getPoster() {
    return poster;
  }
}
