package cm.aptoide.pt.account.view;

import rx.Observable;

public interface PaymentLoginView extends GooglePlayServicesView {

  Observable<Void> backButtonEvents();

  Observable<Void> upNavigationEvents();
}
