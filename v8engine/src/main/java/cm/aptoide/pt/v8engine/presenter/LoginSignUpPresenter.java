package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.LoginSignUpView;

public class LoginSignUpPresenter implements Presenter {

  private final LoginSignUpView view;

  public LoginSignUpPresenter(LoginSignUpView view) {
    this.view = view;
  }

  @Override public void present() {
    // does nothing
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}
