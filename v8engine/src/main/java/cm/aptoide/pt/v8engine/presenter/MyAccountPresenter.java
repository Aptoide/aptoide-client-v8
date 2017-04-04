package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import rx.Observable;

public class MyAccountPresenter implements Presenter {

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick().flatMap(click -> accountManager.logout().doOnCompleted(() -> {
      ManagerPreferences.setAddressBookSyncValues(false);
      view.navigateToHome();
    }).doOnError(throwable -> crashReport.log(throwable)).<Void> toObservable()).retry();
  }
}
