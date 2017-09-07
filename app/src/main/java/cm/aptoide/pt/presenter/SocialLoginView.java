/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.presenter;

import com.facebook.AccessToken;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 08/02/17.
 */
public interface SocialLoginView extends View {

  void showGoogleLogin();

  void hideGoogleLogin();

  void showError(Throwable throwable);

  Observable<Void> googleLoginClick();

  Observable<Void> facebookLoginClick();

  void showPermissionsRequiredMessage();

  void showLoading();

  void hideLoading();

  Single<String> getFacebookUsername(AccessToken accessToken);

  void navigateToMainView();

  void navigateToCreateProfile();

  void navigateToMainViewCleaningBackStack();

  void navigateBack();
}
