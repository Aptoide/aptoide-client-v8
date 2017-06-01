package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface ProfileStepTwoView extends View {
  Observable<Boolean> continueButtonClick();

  Observable<Boolean> makePrivateProfileButtonClick();

  void showWaitDialog();

  void dismissWaitDialog();

  Completable showGenericErrorMessage();

  void navigateToHome();

  void navigateToManageStore();
}
