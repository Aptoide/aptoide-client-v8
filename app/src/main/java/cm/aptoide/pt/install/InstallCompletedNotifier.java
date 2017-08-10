package cm.aptoide.pt.install;

import cm.aptoide.pt.Install;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.crashreports.CrashReport;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trinkes on 27/06/2017.
 */

public class InstallCompletedNotifier {
  private final List<App> appToCheck;
  private final PublishRelay<Void> watcher;
  private final InstallManager installManager;
  private final CrashReport crashReport;

  public InstallCompletedNotifier(PublishRelay<Void> watcher, InstallManager installManager,
      CrashReport crashReport) {
    this.crashReport = crashReport;
    this.appToCheck = new ArrayList<>();
    this.watcher = watcher;
    this.installManager = installManager;
  }

  public void add(String packageName, int versionCode, String md5) {
    App app = new App(packageName, versionCode, md5);
    if (!appToCheck.contains(app)) {
      appToCheck.add(app);
      installManager.getInstall(md5, packageName, versionCode)
          .filter(installationProgress -> installationProgress.getState()
              .equals(Install.InstallationStatus.INSTALLED))
          .first()
          .subscribe(installationProgress -> installFinished(app),
              throwable -> crashReport.log(throwable));
    }
  }

  private void installFinished(App app) {
    appToCheck.remove(app);
    if (appToCheck.isEmpty()) {
      watcher.call(null);
    }
  }

  public PublishRelay<Void> getWatcher() {
    return watcher;
  }

  private class App {
    private String packageName;
    private int versionCode;
    private String md5;

    public App(String packageName, int versionCode, String md5) {
      this.packageName = packageName;
      this.versionCode = versionCode;
      this.md5 = md5;
    }

    @Override public int hashCode() {
      int result = packageName.hashCode();
      result = 31 * result + versionCode;
      result = 31 * result + md5.hashCode();
      return result;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      App app = (App) o;

      if (versionCode != app.versionCode) return false;
      if (!packageName.equals(app.packageName)) return false;
      return md5.equals(app.md5);
    }
  }
}
