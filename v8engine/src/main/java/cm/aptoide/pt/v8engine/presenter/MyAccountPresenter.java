package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.view.MyAccountView;
import cm.aptoide.pt.v8engine.view.View;
import com.google.android.gms.common.api.GoogleApiClient;
import rx.Observable;

public class MyAccountPresenter implements Presenter {

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final GoogleApiClient client;
  private final FragmentManager fragmentManager;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      GoogleApiClient client, FragmentManager fragmentManager) {
    this.view = view;
    this.accountManager = accountManager;
    this.client = client;
    this.fragmentManager = fragmentManager;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick().doOnNext(__ -> {
      signOut();
      ManagerPreferences.setAddressBookSyncValues(false);
      view.navigateToHome();
    });
  }

  private void signOut() {
    accountManager.logout(client);
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}
