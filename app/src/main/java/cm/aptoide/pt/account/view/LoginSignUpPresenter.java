package cm.aptoide.pt.account.view;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.presenter.LoginSignUpView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.BackButton;

public class LoginSignUpPresenter extends BottomSheetBehavior.BottomSheetCallback
    implements Presenter, BackButton.ClickHandler {

  private static final String TAG = LoginSignUpPresenter.class.getName();

  private final LoginSignUpView view;
  private final FragmentNavigator navigatorChild;
  private final boolean dismissToNavigateToMainView;
  private final boolean navigateToHome;

  public LoginSignUpPresenter(LoginSignUpView view, FragmentNavigator navigatorChild,
      boolean dismissToNavigateToMainView, boolean navigateToHome) {
    this.view = view;
    this.navigatorChild = navigatorChild;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
  }

  @Override public void present() {
    view.getLifecycleEvents()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .doOnNext(__ -> {
          LoginSignUpCredentialsFragment fragment = null;
          try {
            fragment = (LoginSignUpCredentialsFragment) navigatorChild.getFragment();
          } catch (ClassCastException ex) {
            Logger.e(TAG, ex);
          }

          if (fragment == null) {
            navigatorChild.navigateToWithoutBackSave(
                LoginSignUpCredentialsFragment.newInstance(dismissToNavigateToMainView,
                    navigateToHome), true);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void onStateChanged(@NonNull android.view.View bottomSheet, int newState) {
    switch (newState) {
      case BottomSheetBehavior.STATE_COLLAPSED:
        view.collapseBottomSheet();
        break;
      case BottomSheetBehavior.STATE_EXPANDED:
        view.expandBottomSheet();
        break;
    }
  }

  @Override public void onSlide(@NonNull android.view.View bottomSheet, float slideOffset) {
  }

  @Override public boolean handle() {
    if (view.bottomSheetIsExpanded()) {
      view.setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
      return true;
    }
    return false;
  }
}
