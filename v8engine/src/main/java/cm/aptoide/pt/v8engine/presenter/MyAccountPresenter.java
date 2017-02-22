package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.v8engine.view.MyAccountView;
import cm.aptoide.pt.v8engine.view.View;
import com.google.android.gms.common.api.GoogleApiClient;
import rx.Observable;

public class MyAccountPresenter implements Presenter {

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final GoogleApiClient client;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator, GoogleApiClient client) {
    this.view = view;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.client = client;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick().doOnNext(__ -> signOut());
  }

  private void signOut() {
    accountManager.logout(client);
    accountNavigator.navigateToAccountView();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}
