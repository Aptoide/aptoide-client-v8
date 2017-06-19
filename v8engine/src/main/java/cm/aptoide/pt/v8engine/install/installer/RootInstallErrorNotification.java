package cm.aptoide.pt.v8engine.install.installer;

import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

/**
 * Created by trinkes on 16/06/2017.
 */

public class RootInstallErrorNotification {

  public final int notificationId;
  private final Bitmap icon;
  private final String message;
  private final NotificationCompat.Action action;

  public RootInstallErrorNotification(int notificationId, Bitmap icon, String message,
      NotificationCompat.Action action) {
    this.notificationId = notificationId;
    this.icon = icon;
    this.message = message;
    this.action = action;
  }

  public Bitmap getIcon() {
    return icon;
  }

  public String getMessage() {
    return message;
  }

  public int getNotificationId() {
    return notificationId;
  }

  public NotificationCompat.Action getAction() {
    return action;
  }
}
