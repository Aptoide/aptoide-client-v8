package cm.aptoide.pt.v8engine;

import android.support.annotation.IntRange;

/**
 * Created by trinkes on 10/04/2017.
 */

public class InstallationProgress {
  private final int progress;
  private final InstallationStatus state;
  private final boolean isIndeterminate;
  private final int speed;
  private final String md5;
  private final String packageName;
  private final int versionCode;

  public InstallationProgress(int progress, InstallationStatus state, boolean isIndeterminate,
      int speed, String md5, String packageName, int versionCode) {
    this.progress = progress;
    this.state = state;
    this.isIndeterminate = isIndeterminate;
    this.speed = speed;
    this.md5 = md5;
    this.packageName = packageName;
    this.versionCode = versionCode;
  }

  public @IntRange(from = 0, to = 100) int getProgress() {
    return progress;
  }

  public InstallationStatus getState() {
    return state;
  }

  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  public int getSpeed() {
    return speed;
  }

  public String getMd5() {
    return md5;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public enum InstallationStatus {
    INSTALLING, PAUSED, INSTALLED, UNINSTALLED, FAILED
  }
}
