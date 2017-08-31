package cm.aptoide.pt.social.data;

import android.support.annotation.NonNull;
import cm.aptoide.pt.notification.AptoideNotification;

/**
 * Created by trinkes on 30/08/2017.
 */

public class NotificationsPost extends DummyPost {
  private AptoideNotification notification;

  public NotificationsPost(@NonNull AptoideNotification notification) {
    this.notification = notification;
  }

  @Override public String getCardId() {
    throw new RuntimeException(this.getClass()
        .getSimpleName() + "  card have NO card id");
  }

  @Override public CardType getType() {
    return CardType.NOTIFICATIONS;
  }

  public String getNotificationImage() {
    return notification.getImg();
  }

  public String getNotificationBody() {
    return notification.getBody();
  }

  public String getUrl() {
    return notification.getUrl();
  }

  public @AptoideNotification.NotificationType Integer[] getNotificationId() {
    return new Integer[] { notification.getType() };
  }
}
