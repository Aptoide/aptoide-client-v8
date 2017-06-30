package cm.aptoide.pt.v8engine.install.installer;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.R;
import java.util.List;

public class RootInstallErrorNotificationFactory {

  private final int notificationId;
  private final Bitmap icon;
  private final NotificationCompat.Action notificationAction;
  private final PendingIntent dismissAction;

  public RootInstallErrorNotificationFactory(int notificationId, Bitmap icon,
      NotificationCompat.Action notificationAction, PendingIntent dismissAction) {
    this.notificationId = notificationId;
    this.icon = icon;
    this.notificationAction = notificationAction;
    this.dismissAction = dismissAction;
  }

  public RootInstallErrorNotification create(Context context, List<Install> installs) {
    return new RootInstallErrorNotification(notificationId, icon,
        getNotificationTitle(context, installs), notificationAction, dismissAction);
  }

  @NonNull private String getNotificationTitle(Context context, List<Install> installs) {
    String title;
    if (installs.size() == 1) {
      title = context.getString(
          R.string.generalscreen_short_root_install_single_app_timeout_error_message);
    } else {
      title = context.getString(R.string.generalscreen_short_root_install_timeout_error_message,
          installs.size());
    }
    return title;
  }
}
