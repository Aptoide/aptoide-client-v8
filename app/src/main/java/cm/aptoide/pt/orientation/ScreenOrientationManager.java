package cm.aptoide.pt.orientation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

public class ScreenOrientationManager {

  private final Activity activity;
  private final WindowManager windowManager;

  public ScreenOrientationManager(Activity activity, WindowManager windowManager) {
    this.activity = activity;
    this.windowManager = windowManager;
  }

  public void lock() {

    int orientation;
    int rotation = windowManager.getDefaultDisplay()
        .getRotation();
    switch (rotation) {
      default:
      case Surface.ROTATION_0:
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        break;
      case Surface.ROTATION_90:
        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        break;
      case Surface.ROTATION_180:
        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        break;
      case Surface.ROTATION_270:
        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        break;
    }

    activity.setRequestedOrientation(orientation);
  }

  public void unlock() {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
  }
}
