/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.account.view.GooglePlayServicesView;
import rx.Observable;

public interface LoginSignUpCredentialsView extends GooglePlayServicesView {

  Observable<Void> showAptoideLoginAreaClick();

  Observable<Void> showAptoideSignUpAreaClick();

  Observable<Void> googleSignUpEvent();

  Observable<Void> showHidePasswordClick();

  Observable<Void> forgotPasswordClick();

  Observable<Void> facebookSignUpWithRequiredPermissionsInEvent();

  Observable<Void> facebookSignUpEvent();

  Observable<AptoideCredentials> aptoideLoginEvent();

  Observable<AptoideCredentials> aptoideSignUpEvent();

  Observable<Void> termsAndConditionsClickEvent();

  Observable<Void> privacyPolicyClickEvent();

  void showAptoideSignUpArea();

  void showAptoideLoginArea();

  void showLoading();

  void hideLoading();

  void showError(String message);

  void showFacebookLogin();

  void showFacebookPermissionsRequiredError(Throwable throwable);

  void hideFacebookLogin();

  void showPassword();

  void hidePassword();

  void dismiss();

  void hideKeyboard();

  void showGoogleLogin();

  void hideGoogleLogin();

  boolean tryCloseLoginBottomSheet();

  boolean isPasswordVisible();

  Context getApplicationContext();

  void lockScreenRotation();

  void unlockScreenRotation();
}
