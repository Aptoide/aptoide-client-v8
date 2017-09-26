package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 26/09/2017.
 */

public class EmptyStatePost extends DummyPost {
  @Override public String getCardId() {
    throw new RuntimeException("Empty state post has no card id");
  }

  @Override public CardType getType() {
    return CardType.EMPTY_STATE;
  }
}
