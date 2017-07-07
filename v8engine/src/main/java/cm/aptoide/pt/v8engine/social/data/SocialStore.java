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
  private final boolean isLiked;

  public SocialStore(String cardId, Poster poster, Long storeId, String storeName,
      String storeAvatar, String socialStore, int subscribers, int appsNumber, Date latestUpdate,
      List<App> apps, String abUrl, boolean isLiked, CardType cardType) {
    super(cardId, storeId, storeName, storeAvatar, socialStore, subscribers, appsNumber,
        latestUpdate, apps, abUrl, false, cardType);
    this.poster = poster;
    this.isLiked = isLiked;
  }

  public Poster getPoster() {
    return poster;
  }

  public boolean isLiked() {
    return isLiked;
  }
}
