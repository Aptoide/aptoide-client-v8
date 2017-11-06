package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import java.util.List;

/**
 * Created by jdandrade on 08/06/2017.
 */

public interface Post {
  String getCardId();

  CardType getType();

  String getAbUrl();

  String getMarkAsReadUrl();

  boolean isLiked();

  void setLiked(boolean liked);

  boolean isLikeFromClick();

  List<SocialCard.CardComment> getComments();

  long getCommentsNumber();

  void addComment(SocialCard.CardComment postComment);
}
