package cm.aptoide.pt.packageinstaller;

import android.os.Bundle;

public interface PackageInstallerResultCallback {

  void onInstallationResult(InstallStatus installStatus);

  void onPendingUserAction(Bundle extras);
}
