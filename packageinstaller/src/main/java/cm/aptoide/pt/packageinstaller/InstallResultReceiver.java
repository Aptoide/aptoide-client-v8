package cm.aptoide.pt.packageinstaller;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;

class InstallResultReceiver extends BroadcastReceiver {

  private final PackageInstallerResultCallback packageInstallerResultCallback;

  public InstallResultReceiver(PackageInstallerResultCallback packageInstallerResultCallback) {
    this.packageInstallerResultCallback = packageInstallerResultCallback;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
  public void onReceive(Context context, Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null && "install_session_api_complete".equals(intent.getAction())) {
      int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
      String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
      switch (status) {
        case PackageInstaller.STATUS_PENDING_USER_ACTION:
          // This app isn't privileged, so the user has to confirm the install.
          packageInstallerResultCallback.onPendingUserAction(extras);
          break;
        case PackageInstaller.STATUS_SUCCESS:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.SUCCESS, "Install succeeded"));
          break;
        case PackageInstaller.STATUS_FAILURE:
        case PackageInstaller.STATUS_FAILURE_ABORTED:
        case PackageInstaller.STATUS_FAILURE_BLOCKED:
        case PackageInstaller.STATUS_FAILURE_CONFLICT:
        case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
        case PackageInstaller.STATUS_FAILURE_INVALID:
        case PackageInstaller.STATUS_FAILURE_STORAGE:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.FAIL,
                  "Install failed " + status + ", " + message));
          break;
        default:
          packageInstallerResultCallback.onInstallationResult(
              new InstallStatus(InstallStatus.Status.UNKNOWN_ERROR,
                  "Unrecognized status received from installer"));
      }
    }
  }
}
