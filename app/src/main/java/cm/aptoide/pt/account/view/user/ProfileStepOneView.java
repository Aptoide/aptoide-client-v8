package cm.aptoide.pt.account.view.user;

import android.support.annotation.NonNull;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;

interface ProfileStepOneView extends View {
  @NonNull Observable<Boolean> continueButtonClick();

  @NonNull Observable<Void> moreInfoButtonClick();

  void showWaitDialog();

  void dismissWaitDialog();

  Completable showGenericErrorMessage();
}
