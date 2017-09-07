package cm.aptoide.pt.view.account.user;

/**
 * Created by danielchen on 07/09/17.
 */

import android.support.annotation.NonNull;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;

interface ProfileStepOneView extends View {
    @NonNull Observable<Boolean> continueButtonClick();

    void showWaitDialog();

    void dismissWaitDialog();

    Completable showGenericErrorMessage();
}
