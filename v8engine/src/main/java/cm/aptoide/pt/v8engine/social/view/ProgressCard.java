package cm.aptoide.pt.v8engine.social.view;

import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.Post;

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
}
