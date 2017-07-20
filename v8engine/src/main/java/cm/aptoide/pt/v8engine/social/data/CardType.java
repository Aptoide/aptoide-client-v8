package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 08/06/2017.
 */

public enum CardType {
  ARTICLE, VIDEO, RECOMMENDATION, STORE, UPDATE, POPULAR_APP, SOCIAL_RECOMMENDATION, SOCIAL_INSTALL, SOCIAL_ARTICLE, SOCIAL_VIDEO, SOCIAL_STORE, AGGREGATED_SOCIAL_ARTICLE, AGGREGATED_SOCIAL_VIDEO, AGGREGATED_SOCIAL_INSTALL, AGGREGATED_SOCIAL_STORE, MINIMAL_CARD, TIMELINE_STATS, LOGIN, SIMILAR, PROGRESS;

  public static boolean isNormal(Post post) {
    return post.getType()
        .equals(CardType.RECOMMENDATION) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.POPULAR_APP) || post.getType()
        .equals(CardType.STORE) || post.getType()
        .equals(CardType.UPDATE);
  }

  public static boolean isSocial(Post post) {
    return post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.SOCIAL_STORE) || post.getType()
        .equals(CardType.SOCIAL_RECOMMENDATION) || post.getType()
        .equals(CardType.SOCIAL_INSTALL);
  }

  public static boolean isMedia(Post post) {
    return post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO);
  }
}
