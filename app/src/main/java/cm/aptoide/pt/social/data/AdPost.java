package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class AdPost extends DummyPost {
  @Override public String getCardId() {
    return CardType.AD.name();
  }

  @Override public CardType getType() {
    return CardType.AD;
  }
}
