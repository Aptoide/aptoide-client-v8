package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineLoginPost implements Post {
  @Override public String getCardId() {
    return "n/a";
  }

  @Override public CardType getType() {
    return CardType.LOGIN;
  }
}
