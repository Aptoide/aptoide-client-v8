package cm.aptoide.pt.v8engine;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import lombok.ToString;

/**
 * Created by trinkes on 10/04/2017.
 */

public @ToString(of = { "state", "isIndeterminate" }) class InstallationProgress {
  private final int progress;
  private final InstallationStatus state;
  private final InstallationType type;
  private final boolean isIndeterminate;
  private final int speed;
  private final String md5;
  private final String packageName;
  private final int versionCode;
  private final String appName;
  private final String icon;
  private final Error error;

  public InstallationProgress(int progress, InstallationStatus state, InstallationType type,
      boolean isIndeterminate, int speed, String md5, String packageName, int versionCode,
      String appName, String icon, Error error) {
    this.progress = progress;
    this.state = state;
    this.type = type;
    this.isIndeterminate = isIndeterminate;
    this.speed = speed;
    this.md5 = md5;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.appName = appName;
    this.icon = icon;
    this.error = error;
  }

  public Error getError() {
    return error;
  }

  public InstallationType getType() {
    return type;
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

  /**
   * @return null if the app is uninstalled and there is no installation in progress
   */
  public @Nullable String getAppName() {
    return appName;
  }

  /**
   * @return null if the app is uninstalled and there is no installation in progress
   */
  public @Nullable String getIcon() {
    return icon;
  }

  @Override public int hashCode() {
    int result = state.hashCode();
    result = 31 * result + md5.hashCode();
    result = 31 * result + packageName.hashCode();
    result = 31 * result + versionCode;
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final InstallationProgress that = (InstallationProgress) o;

    if (versionCode != that.versionCode) return false;
    if (state != that.state) return false;
    if (!md5.equals(that.md5)) return false;
    return packageName.equals(that.packageName);
  }

  public enum InstallationStatus {
    INSTALLING, PAUSED, INSTALLED, UNINSTALLED, FAILED
  }

  public enum Error {
    GENERIC_ERROR, NOT_ENOUGH_SPACE_ERROR, NO_ERROR
  }

  public enum InstallationType {
    INSTALLED, INSTALL, UPDATE, DOWNGRADE
  }
}
