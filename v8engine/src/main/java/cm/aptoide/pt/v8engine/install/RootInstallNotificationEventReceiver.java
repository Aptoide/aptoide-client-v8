package cm.aptoide.pt.v8engine.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;

public class RootInstallNotificationEventReceiver extends BroadcastReceiver {
  public static final String ROOT_INSTALL_RETRY_ACTION = "cm.aptoide.pt.ROOT_INSTALL_RETRY_ACTION";
  public static final String ROOT_INSTALL_DISMISS_ACTION =
      "cm.aptoide.pt.ROOT_INSTALL_DISMISS_ACTION";
  private static final String TAG = RootInstallNotificationEventReceiver.class.getSimpleName();
  private CrashReport crashReport;
  private InstallManager installManager;

  @Override public void onReceive(Context context, Intent intent) {
    installManager =
        ((V8Engine) context.getApplicationContext()).getInstallManager(InstallerFactory.ROLLBACK);

    crashReport = CrashReport.getInstance();

    if (intent != null && intent.getAction() != null) {
      switch (intent.getAction()) {
        case ROOT_INSTALL_RETRY_ACTION:
          installManager.retryTimedOutInstallations(context.getApplicationContext())
              .subscribe(() -> {
              }, throwable -> crashReport.log(throwable));
        case ROOT_INSTALL_DISMISS_ACTION:
          installManager.cleanTimedOutInstalls()
              .subscribe(() -> {
              }, throwable -> crashReport.log(throwable));
          break;
      }
    }
  }
}
