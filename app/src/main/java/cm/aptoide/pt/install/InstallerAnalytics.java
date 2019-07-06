package cm.aptoide.pt.install;

import cm.aptoide.pt.packageinstaller.InstallStatus;

/**
 * Created by trinkes on 30/06/2017.
 */

public interface InstallerAnalytics {
  void rootInstallCompleted(int exitcode);

  void rootInstallTimeout();

  void rootInstallFail(Exception e);

  void rootInstallCancelled();

  void rootInstallStart();

  void installationType(boolean isRootAllowed, boolean isRoot);

  void logInstallErrorEvent(String packageName, int versionCode, Exception e);

  void sendMiuiInstallResultEvent(InstallStatus.Status status);
}
