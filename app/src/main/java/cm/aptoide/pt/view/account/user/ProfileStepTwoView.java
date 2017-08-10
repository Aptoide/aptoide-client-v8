package cm.aptoide.pt.view.account.user;

import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;

interface ProfileStepTwoView extends View {
  Observable<Boolean> continueButtonClick();

  Observable<Boolean> makePrivateProfileButtonClick();

  void showWaitDialog();

  void dismissWaitDialog();

  Completable showGenericErrorMessage();
}
