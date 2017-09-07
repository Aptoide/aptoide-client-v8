/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.view.account.AptoideAccountViewModel;
import rx.Observable;

/**
 * Created by marcelobenites on 07/02/17.
 */

public interface LoginSignUpCredentialsView extends SocialLoginView {

  Observable<Void> showAptoideLoginAreaClick();

  Observable<Void> showAptoideSignUpAreaClick();

  void showAptoideSignUpArea();

  void showAptoideLoginArea();

  void showFacebookLogin();

  void hideFacebookLogin();

  void showForgotPasswordView();

  void showPassword();

  void hidePassword();

  Observable<Void> showHidePasswordClick();

  Observable<Void> forgotPasswordClick();

  void dismiss();

  void hideKeyboard();

  Observable<AptoideAccountViewModel> aptoideLoginClick();

  Observable<AptoideAccountViewModel> aptoideSignUpClick();

  boolean tryCloseLoginBottomSheet();

  @NonNull AptoideAccountViewModel getCredentials();

  boolean isPasswordVisible();

  Context getApplicationContext();

  void lockScreenRotation();

  void unlockScreenRotation();
}
