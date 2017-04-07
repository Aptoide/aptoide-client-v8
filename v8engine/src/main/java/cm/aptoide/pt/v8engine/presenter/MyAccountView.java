package cm.aptoide.pt.v8engine.presenter;

import rx.Observable;

public interface MyAccountView extends View {
  Observable<Void> signOutClick();

  void navigateToHome();
}
