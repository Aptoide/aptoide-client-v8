package cm.aptoide.pt.social.view;

import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.DummyPost;

/**
 * Created by jdandrade on 19/06/2017.
 */

public class ProgressCard extends DummyPost {
  @Override public String getCardId() {
    return CardType.PROGRESS.name();
  }

  @Override public CardType getType() {
    return CardType.PROGRESS;
  }
}
