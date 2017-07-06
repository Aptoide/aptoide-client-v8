/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface WebView extends View {

  void showLoading();

  void hideLoading();

  void loadWebsite(String url, String redirectUrl);

  Observable<Void> redirectUrlEvent();

  Observable<Void> backButtonEvent();

  Observable<Void> urlLoadedEvent();

  void showError();

  Observable<Void> errorDismissedEvent();
}
