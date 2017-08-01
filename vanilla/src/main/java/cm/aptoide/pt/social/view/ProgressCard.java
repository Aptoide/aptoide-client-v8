package cm.aptoide.pt.social.view;

import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.Post;

/**
 * Created by jdandrade on 19/06/2017.
 */

public class ProgressCard implements Post {
  @Override public String getCardId() {
    return CardType.PROGRESS.name();
  }

  @Override public CardType getType() {
    return CardType.PROGRESS;
  }

  @Override public String getAbUrl() {
    //supposed to be null
    return null;
  }

  @Override public boolean isLiked() {
    return false;
  }

  @Override public void setLiked(boolean liked) {
    //do nothing
  }

  @Override public boolean isLikeFromClick() {
    return false;
  }
}
