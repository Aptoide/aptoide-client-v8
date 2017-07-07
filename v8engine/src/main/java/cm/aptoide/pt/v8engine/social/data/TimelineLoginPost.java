package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineLoginPost implements Post {
  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }

  @Override public CardType getType() {
    return CardType.LOGIN;
  }
}
