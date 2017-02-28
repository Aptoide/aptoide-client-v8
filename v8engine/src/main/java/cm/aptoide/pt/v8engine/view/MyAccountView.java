package cm.aptoide.pt.v8engine.view;

import rx.Observable;

public interface MyAccountView extends View {
  Observable<Void> signOutClick();

  void navigateToLoginAfterLogout();
}
