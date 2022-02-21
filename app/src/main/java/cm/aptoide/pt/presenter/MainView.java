/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Intent;
import cm.aptoide.pt.home.AcceptTermsAndConditionsDialog;
import cm.aptoide.pt.home.AcceptTermsAndConditionsDialog.AcceptTermsAndConditionsClickType;
import rx.Observable;

/**
 * Created by marcelobenites on 18/01/17.
 */

public interface MainView extends View {

  void showInstallationError(int numberOfErrors);

  void dismissInstallationError();

  void showInstallationSuccessMessage();

  Observable<Void> getInstallErrorsDismiss();

  Intent getIntentAfterCreate();

  void showUnknownErrorMessage();

  void dismissAutoUpdateDialog();

  void showLoadingView();

  void hideLoadingView();

  void showGenericErrorMessage();

  Observable<String> onAuthenticationIntent();

  void showUpdatesBadge(int updates);

  void showTermsAndConditionsDialog();

  Observable<AcceptTermsAndConditionsClickType> acceptedTermsAndConditions();
}
