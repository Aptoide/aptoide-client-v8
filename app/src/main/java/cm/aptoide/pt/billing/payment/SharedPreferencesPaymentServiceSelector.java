/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 17/02/2017.
 */

package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.PaymentServiceSelector;
import cm.aptoide.pt.preferences.Preferences;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class SharedPreferencesPaymentServiceSelector implements PaymentServiceSelector {

  private static final String SELECTED_SERVICE_TYPE = "SELECTED_SERVICE_TYPE";
  private final String defaultServiceType;
  private final Preferences preferences;

  public SharedPreferencesPaymentServiceSelector(String defaultServiceType,
      Preferences preferences) {
    this.defaultServiceType = defaultServiceType;
    this.preferences = preferences;
  }

  @Override public Observable<PaymentService> getSelectedService(List<PaymentService> services) {
    return getSelectedServiceName().flatMapSingle(
        serviceName -> getService(services, serviceName).switchIfEmpty(
            getService(services, defaultServiceType))
            .switchIfEmpty(Observable.just(services.get(0)))
            .first()
            .toSingle());
  }

  @Override public Completable selectService(PaymentService service) {
    return preferences.save(SELECTED_SERVICE_TYPE, service.getType());
  }

  private Observable<String> getSelectedServiceName() {
    return preferences.getString(SELECTED_SERVICE_TYPE, null);
  }

  private Observable<PaymentService> getService(List<PaymentService> services, String serviceName) {
    return Observable.from(services)
        .filter(service -> serviceName != null && serviceName.equals(service.getType()));
  }
}
