package cm.aptoide.pt.v8engine.social.view;

import cm.aptoide.pt.v8engine.social.data.CardType;

/**
 * Created by jdandrade on 19/06/2017.
 */

public class ProgressCard implements cm.aptoide.pt.v8engine.social.data.Card {
  @Override public String getCardId() {
    return CardType.PROGRESS.name();
  }

  @Override public CardType getType() {
    return CardType.PROGRESS;
  }
}
