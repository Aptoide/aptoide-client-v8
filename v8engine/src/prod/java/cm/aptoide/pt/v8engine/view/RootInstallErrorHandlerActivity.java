package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceActivity;

/**
 * Created by trinkes on 16/06/2017.
 */

public class RootInstallErrorHandlerActivity extends PermissionServiceActivity {
  @Override protected void onResume() {
    super.onResume();

    InstallManager installManager =
        ((V8Engine) getApplicationContext()).getInstallManager(InstallerFactory.ROLLBACK);

    ((V8Engine) getApplicationContext()).getRootInstallationRetryHandler()
        .retries()
        .distinctUntilChanged(rootInstallTimeoutError -> rootInstallTimeoutError.getMessage())
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(installNotifications -> ShowMessage.asSnackIndefiniteTime(this,
            installNotifications.getMessage(),
            R.string.generalscreen_short_root_install_timeout_error_action,
            view -> installManager.retryTimedOutInstallations(this)
                .subscribe()));
  }
}
