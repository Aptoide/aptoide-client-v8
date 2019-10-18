package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.exception.InstallationException;
import cm.aptoide.pt.logger.Logger;
import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rx.Observable;
import rx.Subscriber;

public class RootInstaller implements Observable.OnSubscribe<Void> {

  private static final String TAG = "RootInstaller";
  private final String packageName;
  private final InstallerAnalytics analytics;
  private Installation installation;
  private Root root;

  public RootInstaller(String packageName, InstallerAnalytics analytics,
      Installation installation) {
    this.packageName = packageName;
    this.analytics = analytics;
    this.installation = installation;
    root = new Root();
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    Logger.getInstance()
        .d(TAG, "call: start with installation package name: " + installation.getPackageName());

    analytics.rootInstallStart();
    if (root.isTerminated() || !root.isAcquired()) {
      Root.requestRoot();
      if (!root.isAcquired()) {
        Logger.getInstance()
            .d(TAG, "root is not available");
        subscriber.onError(new InstallationException("No root permissions"));
        return;
      }
    }

    String commandResult = root.exec(String.format(Locale.getDefault(),
        "pm install-create -i " + packageName + " --user %s -r -S %d", "0",
        getFilesSize(installation)));

    if (commandResult == null || commandResult.length() == 0) {
      InstallationException installationException = new InstallationException(root.readError());
      analytics.rootInstallFail(installationException);
      subscriber.onError(installationException);
      return;
    }

    Pattern sessionIdPattern = Pattern.compile("(\\d+)");
    Matcher sessionIdMatcher = sessionIdPattern.matcher(commandResult);
    sessionIdMatcher.find();
    int sessionId = Integer.parseInt(sessionIdMatcher.group(1));

    for (FileToDownload apkFile : installation.getFiles()) {
      Logger.getInstance()
          .d(TAG, "started instalation of file: " + apkFile.getFileName());
      File file = new File(apkFile.getPath() + apkFile.getFileName());

      String fileResult = root.exec(
          String.format(Locale.getDefault(), "cat \"%s\" | pm install-write -S %d %d \"%s\"",
              file.getAbsolutePath(), file.length(), sessionId, file.getName()));
      if (fileResult == null || fileResult.length() == 0) {
        InstallationException installationException = new InstallationException(root.readError());
        analytics.rootInstallFail(installationException);
        subscriber.onError(installationException);
        return;
      }
    }

    String commitResult =
        root.exec(String.format(Locale.getDefault(), "pm install-commit %d ", sessionId));
    if (commitResult == null || commitResult.length() == 0) {
      InstallationException installationException = new InstallationException(root.readError());
      analytics.rootInstallFail(installationException);
      subscriber.onError(installationException);
      return;
    }

    if (commitResult.toLowerCase()
        .contains("success")) {
      if (!subscriber.isUnsubscribed()) {
        analytics.rootInstallCompleted(0);
        subscriber.onCompleted();
        return;
      }
    } else {
      InstallationException installationException = new InstallationException(root.readError());
      analytics.rootInstallFail(installationException);
      subscriber.onError(installationException);
      return;
    }
  }

  private int getFilesSize(Installation installation) {
    int totalSize = 0;
    for (FileToDownload apkFile : installation.getFiles()) {
      File file = new File(apkFile.getPath() + apkFile.getFileName());
      totalSize += file.length();
    }
    return totalSize;
  }
}
