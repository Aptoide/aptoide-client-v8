package cm.aptoide.pt.social.data;

/**
 * Created by trinkes on 31/08/2017.
 */

public class TimelineNoNotificationHeader extends DummyPost {

  @Override public String getCardId() {
    throw new RuntimeException(this.getClass()
        .getSimpleName() + "  card have NO card id");
  }

  @Override public CardType getType() {
    return CardType.NO_NOTIFICATIONS;
  }
}
