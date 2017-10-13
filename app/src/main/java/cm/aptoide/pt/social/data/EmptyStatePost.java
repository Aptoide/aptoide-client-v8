package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 26/09/2017.
 */

public class EmptyStatePost extends DummyPost {
  public static final int ACTION = 0;
  public static final int NO_ACTION = 1;
  private int action;

  public EmptyStatePost() {
  }

  @Override public String getCardId() {
    throw new RuntimeException("Empty state post has no card id");
  }

  @Override public CardType getType() {
    return CardType.EMPTY_STATE;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }
}
