/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.gms.GooglePlayServicesConnection;
import cm.aptoide.pt.v8engine.view.GoogleLoginView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class GoogleLoginPresenter implements Presenter {

  private final GoogleLoginView view;
  private final GooglePlayServicesConnection playServicesConnection;
  private final AptoidePreferencesConfiguration configuration;
  private final AptoideAccountManager accountManager;

  public GoogleLoginPresenter(GoogleLoginView view,
      GooglePlayServicesConnection playServicesConnection,
      AptoidePreferencesConfiguration configuration, AptoideAccountManager accountManager) {
    this.view = view;
    this.playServicesConnection = playServicesConnection;
    this.configuration = configuration;
    this.accountManager = accountManager;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> playServicesConnection.connect())
        .doOnNext(created -> showOrHideGoogleCredentialsSelector())
        .flatMap(resumed -> Observable.merge(googleLoginSelection(), googleCredentialsSelection()))
        .doOnUnsubscribe(() -> playServicesConnection.disconnect())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> googleLoginSelection() {
    return view.googleLoginSelection()
        .doOnNext(selected -> view.showLoading())
        .<Void>flatMap(
        credentialsViewModel -> accountManager.login(LoginMode.GOOGLE,
            credentialsViewModel.getEmail(), credentialsViewModel.getToken(),
            credentialsViewModel.getName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> view.showError(throwable))
            .toObservable())
        .retry();
  }

  private void showOrHideGoogleCredentialsSelector() {
    if (playServicesConnection.isAvailable() && configuration.isLoginAvailable(
        AptoidePreferencesConfiguration.SocialLogin.GOOGLE)) {
      view.showGoogleCredentialsSelector();
    } else {
      view.hideGoogleCredentialsSelector();
    }
  }

  private Observable<GooglePlayServicesConnection.Status> googleCredentialsSelection() {
    return view.googleCredentialsSelection()
        .doOnNext(selected -> playServicesConnection.connect())
        .flatMap(selected -> navigateToCredentialsViewOrShowError())
        .retry();
  }

  private Observable<GooglePlayServicesConnection.Status> navigateToCredentialsViewOrShowError() {
    return playServicesConnection.getStatus()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(status -> {
          if (status.getCode() == GooglePlayServicesConnection.Status.ERROR) {
            showErrorOrResolution(status);
          }
          if (status.getCode() == GooglePlayServicesConnection.Status.CONNECTED) {
            view.navigateToGoogleCredentialsView();
          }
        })
        .takeUntil(status -> status.getCode() == GooglePlayServicesConnection.Status.ERROR
            || status.getCode() == GooglePlayServicesConnection.Status.CONNECTED);
  }

  private void showErrorOrResolution(GooglePlayServicesConnection.Status status) {
    if (status.isResolvable()) {
      view.showResolution(status.getErrorCode());
    } else {
      view.showConnectionErrorMessage(status.getErrorCode());
    }
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
