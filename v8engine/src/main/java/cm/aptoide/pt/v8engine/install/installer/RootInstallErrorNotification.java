package cm.aptoide.pt.v8engine.install.installer;

import android.graphics.Bitmap;

/**
 * Created by trinkes on 16/06/2017.
 */

public class RootInstallErrorNotification {

  public final int notificationId;
  private final Bitmap icon;
  private final String message;

  public RootInstallErrorNotification(int notificationId, Bitmap icon, String message) {
    this.notificationId = notificationId;
    this.icon = icon;
    this.message = message;
  }

  public Bitmap getIcon() {
    return icon;
  }

  public String getMessage() {
    return message;
  }

  public int getId() {
    return notificationId;
  }
}
