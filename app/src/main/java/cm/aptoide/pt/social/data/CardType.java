package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 08/06/2017.
 */

public enum CardType {
  ARTICLE, VIDEO, RECOMMENDATION, STORE, UPDATE, POPULAR_APP, SOCIAL_RECOMMENDATION, SOCIAL_INSTALL, SOCIAL_ARTICLE, SOCIAL_VIDEO, SOCIAL_STORE, AGGREGATED_SOCIAL_ARTICLE, AGGREGATED_SOCIAL_VIDEO, AGGREGATED_SOCIAL_INSTALL, AGGREGATED_SOCIAL_STORE, MINIMAL_CARD, TIMELINE_STATS, LOGIN, SIMILAR, PROGRESS, SOCIAL_POST_ARTICLE, SOCIAL_POST_VIDEO, SOCIAL_POST_RECOMMENDATION, AD, NOTIFICATIONS, NO_NOTIFICATIONS, AGGREGATED_SOCIAL_APP, GAMETEXT, GAMEICON, GAMETEXTICON, GAMEANSWER, EMPTY_STATE;

  public boolean isNormal() {
    return equals(RECOMMENDATION) || equals(ARTICLE) || equals(SIMILAR) || equals(VIDEO) || equals(
        POPULAR_APP) || equals(STORE) || equals(UPDATE);
  }

  public boolean isSocial() {
    return equals(SOCIAL_ARTICLE) || equals(SOCIAL_VIDEO) || equals(SOCIAL_STORE) || equals(
        SOCIAL_RECOMMENDATION) || equals(SOCIAL_INSTALL) || equals(SOCIAL_POST_ARTICLE) || equals(
        SOCIAL_POST_VIDEO) || equals(SOCIAL_POST_RECOMMENDATION);
  }

  public boolean isMedia() {
    return equals(VIDEO)
        || equals(ARTICLE)
        || equals(SOCIAL_ARTICLE)
        || equals(SOCIAL_VIDEO)
        || equals(AGGREGATED_SOCIAL_ARTICLE)
        || equals(AGGREGATED_SOCIAL_VIDEO)
        || equals(SOCIAL_POST_VIDEO)
        || equals(SOCIAL_POST_ARTICLE);
  }

  public boolean isVideo() {
    return equals(VIDEO) || equals(SOCIAL_VIDEO) || equals(AGGREGATED_SOCIAL_VIDEO) || equals(
        SOCIAL_POST_VIDEO);
  }

  public boolean isArticle() {
    return equals(ARTICLE) || equals(SOCIAL_ARTICLE) || equals(AGGREGATED_SOCIAL_ARTICLE) || equals(
        SOCIAL_POST_ARTICLE);
  }

  public boolean isAggregated() {
    return equals(AGGREGATED_SOCIAL_ARTICLE) || equals(AGGREGATED_SOCIAL_INSTALL) || equals(
        AGGREGATED_SOCIAL_STORE) || equals(AGGREGATED_SOCIAL_STORE) || equals(
        AGGREGATED_SOCIAL_VIDEO);
  }

  public boolean isDummy() {
    return equals(LOGIN) || equals(PROGRESS) || equals(TIMELINE_STATS);
  }

  public boolean isGame(){return equals(GAMETEXT) || equals(GAMEICON) || equals(GAMETEXTICON);}
}
