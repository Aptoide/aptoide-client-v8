package cm.aptoide.pt.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface PaymentServiceSelector {

  Single<PaymentService> selectedService(List<PaymentService> paymentServices);

  Completable selectService(PaymentService selectedPaymentService);
}
