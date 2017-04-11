/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.v8engine.view.account.AptoideAccountViewModel;
import cm.aptoide.pt.v8engine.view.account.FacebookAccountViewModel;
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

  void navigateToForgotPasswordView();

  void showPassword();

  void hidePassword();

  Observable<Void> showHidePasswordClick();

  Observable<Void> forgotPasswordClick();

  void navigateToMainView();

  void goBack();

  void dismiss();

  void hideKeyboard();

  Observable<FacebookAccountViewModel> facebookLoginClick();

  Observable<AptoideAccountViewModel> aptoideLoginClick();

  Observable<AptoideAccountViewModel> aptoideSignUpClick();

  boolean isPasswordVisible();

  void navigateToCreateProfile();
}
