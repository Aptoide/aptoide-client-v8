/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.v8engine.view.LoginView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class GoogleLoginPresenter implements Presenter {

  private final LoginView view;
  private final AptoideAccountManager accountManager;

  public GoogleLoginPresenter(LoginView view, AptoideAccountManager accountManager) {
    this.view = view;
    this.accountManager = accountManager;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> showOrHideGoogleLogin())
        .flatMap(resumed -> googleLoginSelection())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> googleLoginSelection() {
    return view.googleLoginSelection().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> accountManager.login(LoginMode.GOOGLE, credentials.getUsername(),
            credentials.getPassword(), credentials.getDisplayName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> view.showError(throwable))
            .toObservable()).retry();
  }

  private void showOrHideGoogleLogin() {
    if (accountManager.isGoogleLoginEnabled()) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
