package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 08/06/2017.
 */

public interface Post {
  String getCardId();

  CardType getType();

  String getAbUrl();

  boolean isLiked();

  void setLiked(boolean liked);

  boolean isLikeFromClick();
}
