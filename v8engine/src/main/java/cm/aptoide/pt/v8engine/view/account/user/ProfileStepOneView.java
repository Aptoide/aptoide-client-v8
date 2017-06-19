package cm.aptoide.pt.v8engine.view.account.user;

import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface ProfileStepOneView extends View {
  @NonNull Observable<Boolean> continueButtonClick();

  @NonNull Observable<Void> moreInfoButtonClick();

  void showWaitDialog();

  void dismissWaitDialog();

  Completable showGenericErrorMessage();
}
