package cm.aptoide.pt.install;

import android.os.Build;
import cm.aptoide.pt.install.installer.Installation;
import cm.aptoide.pt.install.installer.RootCommandOnSubscribe;
import cm.aptoide.pt.install.installer.RootInstaller;
import java.io.File;
import rx.Observable;

public class RootInstallerProvider {

  private final String packageName;
  private InstallerAnalytics installerAnalytics;

  public RootInstallerProvider(InstallerAnalytics installerAnalytics, String packageName) {
    this.installerAnalytics = installerAnalytics;
    this.packageName = packageName;
  }

  public Observable.OnSubscribe<Void> provideRootInstaller(Installation installation) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return new RootInstaller(packageName, installation);
    } else {
      return new RootCommandOnSubscribe(installation.getId()
          .hashCode(), getRootInstallCommand(installation), installerAnalytics);
    }
  }

  private String getRootInstallCommand(Installation installation) {
    File file = installation.getFile();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      return "cat " + file.getAbsolutePath() + " | pm install -S " + file.length();
    }
    return "pm install -r " + file.getAbsolutePath();
  }
}
