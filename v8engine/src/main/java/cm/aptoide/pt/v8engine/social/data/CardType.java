package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 08/06/2017.
 */

public enum CardType {
  ARTICLE, VIDEO, RECOMMENDATION, STORE, UPDATE, POPULAR_APP, SOCIAL_RECOMMENDATION, SOCIAL_INSTALL, SOCIAL_ARTICLE, SOCIAL_VIDEO, SOCIAL_STORE, AGGREGATED_SOCIAL_ARTICLE, AGGREGATED_SOCIAL_VIDEO, AGGREGATED_SOCIAL_INSTALL, AGGREGATED_SOCIAL_STORE, MINIMAL_CARD, TIMELINE_STATS, LOGIN, SIMILAR, PROGRESS;

  public static boolean isNormal(CardType cardType) {
    return cardType.equals(CardType.RECOMMENDATION)
        || cardType.equals(CardType.ARTICLE)
        || cardType.equals(CardType.VIDEO)
        || cardType.equals(CardType.POPULAR_APP)
        || cardType.equals(CardType.STORE)
        || cardType.equals(CardType.UPDATE);
  }

  public static boolean isSocial(CardType cardType) {
    return cardType.equals(CardType.SOCIAL_ARTICLE)
        || cardType.equals(CardType.SOCIAL_VIDEO)
        || cardType.equals(CardType.SOCIAL_STORE)
        || cardType.equals(CardType.SOCIAL_RECOMMENDATION)
        || cardType.equals(CardType.SOCIAL_INSTALL);
  }

  public static boolean isMedia(CardType cardType) {
    return cardType.equals(CardType.VIDEO) || cardType.equals(CardType.ARTICLE) || cardType.equals(
        CardType.SOCIAL_ARTICLE) || cardType.equals(CardType.SOCIAL_VIDEO) || cardType.equals(
        CardType.AGGREGATED_SOCIAL_ARTICLE) || cardType.equals(CardType.AGGREGATED_SOCIAL_VIDEO);
  }
}
