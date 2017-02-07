/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.view;

import rx.Observable;

/**
 * Created by marcelobenites on 06/02/17.
 */
public interface GooglePlayServicesView extends View {

  Observable<Void> onGoogleLoginSelection();

  void showGoogleLogin();

  void hideGoogleLogin();

  void showResolution(int errorCode);

  void showConnectionErrorMessage(int errorCode);

  void showSuccess();
}
