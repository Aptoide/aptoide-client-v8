/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 17/02/2017.
 */

package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class SharedPreferencesPaymentServiceSelector implements PaymentServiceSelector {

  private static final String SELECTED_SERVICE_TYPE = "SELECTED_SERVICE_TYPE";
  private final String defaultServiceType;
  private final SharedPreferences preferences;

  public SharedPreferencesPaymentServiceSelector(String defaultServiceType,
      SharedPreferences preferences) {
    this.defaultServiceType = defaultServiceType;
    this.preferences = preferences;
  }

  @Override public Single<PaymentService> selectedService(List<PaymentService> services) {
    return getSelectedServiceName().flatMap(
        serviceName -> getService(services, serviceName).switchIfEmpty(
            getService(services, defaultServiceType))
            .switchIfEmpty(Observable.just(services.get(0)))
            .first()
            .toSingle());
  }

  @Override public Completable selectService(PaymentService service) {
    return Completable.fromAction(() -> preferences.edit()
        .putString(SELECTED_SERVICE_TYPE, service.getType())
        .commit())
        .subscribeOn(Schedulers.io());
  }

  private Single<String> getSelectedServiceName() {
    return Single.fromCallable(() -> preferences.getString(SELECTED_SERVICE_TYPE, null))
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaymentService> getService(List<PaymentService> services, String serviceName) {
    return Observable.from(services)
        .filter(service -> serviceName != null && serviceName.equals(service.getType()));
  }
}
