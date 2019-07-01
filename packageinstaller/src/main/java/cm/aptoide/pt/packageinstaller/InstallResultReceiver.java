package cm.aptoide.pt.packageinstaller;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;

import static cm.aptoide.pt.packageinstaller.AppInstaller.INSTALL_SESSION_API_COMPLETE_ACTION;

class InstallResultReceiver extends BroadcastReceiver {

  private final PackageInstallerResultCallback packageInstallerResultCallback;

  public InstallResultReceiver(PackageInstallerResultCallback packageInstallerResultCallback) {
    this.packageInstallerResultCallback = packageInstallerResultCallback;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
  public void onReceive(Context context, Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null && INSTALL_SESSION_API_COMPLETE_ACTION.equals(intent.getAction())) {
      int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
      String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
      String packageName = extras.getString(PackageInstaller.EXTRA_PACKAGE_NAME);
      switch (status) {
        case PackageInstaller.STATUS_PENDING_USER_ACTION:
          packageInstallerResultCallback.onPendingUserAction(extras);
          break;
        case PackageInstaller.STATUS_SUCCESS:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.SUCCESS, "Install succeeded", packageName));
          break;
        case PackageInstaller.STATUS_FAILURE:
        case PackageInstaller.STATUS_FAILURE_BLOCKED:
        case PackageInstaller.STATUS_FAILURE_CONFLICT:
        case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
        case PackageInstaller.STATUS_FAILURE_INVALID:
        case PackageInstaller.STATUS_FAILURE_STORAGE:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.FAIL,
                  "Install failed " + status + ", " + message, packageName));
          break;
        case PackageInstaller.STATUS_FAILURE_ABORTED:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.CANCELED,
                  "Install failed " + status + ", " + message));
        default:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.UNKNOWN_ERROR,
                  "Unrecognized status received from installer", packageName));
      }
    }
  }
}
