package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface MyAccountView extends View {
  Observable<Void> signOutClick();

  void navigateToHome();
}
