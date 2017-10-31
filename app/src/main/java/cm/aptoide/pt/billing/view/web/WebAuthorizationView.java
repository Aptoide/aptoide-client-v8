/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.billing.view.web;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface WebAuthorizationView extends View {

  void showLoading();

  void hideLoading();

  void loadWebsite(String url, String redirectUrl);

  Observable<Void> redirectUrlEvent();

  Observable<Void> loadUrlErrorEvent();

  Observable<Void> backButtonEvent();

  void showError();

  Observable<Void> errorDismissedEvent();
}
