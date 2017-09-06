/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.view.account.AptoideResult;
import com.facebook.login.LoginResult;
import rx.Observable;

/**
 * Created by marcelobenites on 07/02/17.
 */

public interface LoginSignUpCredentialsView extends GoogleLoginView {

  Observable<Void> showAptoideLoginAreaClick();

  Observable<Void> showAptoideSignUpAreaClick();

  void showAptoideSignUpArea();

  void showAptoideLoginArea();

  void showLoading();

  void hideLoading();

  void showError(Throwable throwable);

  void showFacebookLogin();

  void showPermissionsRequiredMessage();

  void hideFacebookLogin();

  void showForgotPasswordView();

  void showPassword();

  void hidePassword();

  Observable<Void> showHidePasswordClick();

  Observable<Void> forgotPasswordClick();

  void dismiss();

  void hideKeyboard();

  void showGoogleLogin();

  void hideGoogleLogin();

  void showFacebookLoginError();

  void showFacebookCancelledError();

  void showGoogleLoginError();

  Observable<LoginResult> facebookLoginClick();

  Observable<AptoideResult> aptoideLoginClick();

  Observable<AptoideResult> aptoideSignUpClick();

  boolean tryCloseLoginBottomSheet();

  @NonNull AptoideResult getCredentials();

  boolean isPasswordVisible();

  Context getApplicationContext();

  void lockScreenRotation();

  void unlockScreenRotation();
}
