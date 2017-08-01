package cm.aptoide.pt.install.installer;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

public class RootInstallErrorNotification {

  public final int notificationId;
  private final Bitmap icon;
  private final String message;
  private final NotificationCompat.Action action;
  private final PendingIntent deleteAction;

  public RootInstallErrorNotification(int notificationId, Bitmap icon, String message,
      NotificationCompat.Action action, PendingIntent deleteAction) {
    this.notificationId = notificationId;
    this.icon = icon;
    this.message = message;
    this.action = action;
    this.deleteAction = deleteAction;
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

  public PendingIntent getDeleteAction() {
    return deleteAction;
  }
}
