/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Context;
import cm.aptoide.pt.account.view.GooglePlayServicesView;
import rx.Observable;

public interface LoginSignUpCredentialsView extends GooglePlayServicesView {

  Observable<Boolean> showAptoideLoginAreaClick();

  Observable<Void> googleSignUpEvent();

  Observable<Void> facebookSignUpWithRequiredPermissionsInEvent();

  Observable<Void> facebookSignUpEvent();

  Observable<Void> termsAndConditionsClickEvent();

  Observable<Void> privacyPolicyClickEvent();

  void showAptoideLoginArea();

  void showLoading();

  void hideLoading();

  void showError(String message);

  void showTermsConditionError();

  void showFacebookLogin();

  void showFacebookPermissionsRequiredError(Throwable throwable);

  void hideFacebookLogin();

  void dismiss();

  void hideKeyboard();

  void showGoogleLogin();

  void hideGoogleLogin();

  boolean tryCloseLoginBottomSheet(boolean shouldShowTCandPP);

  Context getApplicationContext();

  void lockScreenRotation();

  void unlockScreenRotation();

  void setCobrandText();

  void showTCandPP();
}
