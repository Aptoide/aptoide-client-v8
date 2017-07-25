package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 08/06/2017.
 */

public enum CardType {
  ARTICLE, VIDEO, RECOMMENDATION, STORE, UPDATE, POPULAR_APP, SOCIAL_RECOMMENDATION, SOCIAL_INSTALL, SOCIAL_ARTICLE, SOCIAL_VIDEO, SOCIAL_STORE, AGGREGATED_SOCIAL_ARTICLE, AGGREGATED_SOCIAL_VIDEO, AGGREGATED_SOCIAL_INSTALL, AGGREGATED_SOCIAL_STORE, MINIMAL_CARD, TIMELINE_STATS, LOGIN, SIMILAR, PROGRESS;

  public boolean isNormal() {
    return equals(CardType.RECOMMENDATION)
        || equals(CardType.ARTICLE)
        || equals(CardType.VIDEO)
        || equals(CardType.POPULAR_APP)
        || equals(CardType.STORE)
        || equals(CardType.UPDATE);
  }

  public boolean isSocial() {
    return equals(CardType.SOCIAL_ARTICLE) || equals(CardType.SOCIAL_VIDEO) || equals(
        CardType.SOCIAL_STORE) || equals(CardType.SOCIAL_RECOMMENDATION) || equals(
        CardType.SOCIAL_INSTALL);
  }

  public boolean isMedia() {
    return equals(CardType.VIDEO)
        || equals(CardType.ARTICLE)
        || equals(CardType.SOCIAL_ARTICLE)
        || equals(CardType.SOCIAL_VIDEO)
        || equals(CardType.AGGREGATED_SOCIAL_ARTICLE)
        || equals(CardType.AGGREGATED_SOCIAL_VIDEO);
  }

  public boolean isVideo() {
    return equals(VIDEO) || equals(SOCIAL_VIDEO) || equals(AGGREGATED_SOCIAL_VIDEO);
  }

  public boolean isArticle() {
    return equals(ARTICLE) || equals(SOCIAL_ARTICLE) || equals(AGGREGATED_SOCIAL_ARTICLE);
  }
}
