/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.gms.GooglePlayServicesException;
import cm.aptoide.pt.v8engine.gms.GooglePlayServicesConnection;
import cm.aptoide.pt.v8engine.view.GooglePlayServicesView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class GoogleLoginPresenter implements Presenter {

  private final GooglePlayServicesView view;
  private final GooglePlayServicesConnection playServicesConnection;

  public GoogleLoginPresenter(GooglePlayServicesView view,
      GooglePlayServicesConnection playServicesConnection) {
    this.view = view;
    this.playServicesConnection = playServicesConnection;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> playServicesConnection.isAvailable().toObservable())
        .doOnNext(available -> {
          if (available) {
            view.showGoogleLogin();
          } else {
            view.hideGoogleLogin();
          }
        })
        .filter(available -> available)
        .flatMap(available -> view.onGoogleLoginSelection()
            .flatMap(selected -> playServicesConnection.connect()
                .doOnCompleted(() -> view.showSuccess())
                .doOnError(throwable -> showError(throwable))
                .onErrorComplete()
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showError(Throwable throwable) {
    if (throwable instanceof GooglePlayServicesException) {
      if (((GooglePlayServicesException) throwable).isResolvable()) {
        view.showResolution(((GooglePlayServicesException) throwable).getErrorCode());
      } else {
        view.showConnectionErrorMessage(((GooglePlayServicesException) throwable).getErrorCode());
      }
    }
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
