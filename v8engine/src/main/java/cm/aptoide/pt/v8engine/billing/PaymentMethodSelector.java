package cm.aptoide.pt.v8engine.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface PaymentMethodSelector {

  Single<PaymentMethod> selectedPaymentMethod(List<PaymentMethod> paymentMethods);

  Completable selectPaymentMethod(PaymentMethod selectedPaymentMethod);
}
