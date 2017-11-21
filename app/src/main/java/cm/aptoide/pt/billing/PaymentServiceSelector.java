package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.payment.PaymentService;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface PaymentServiceSelector {

  Observable<PaymentService> getSelectedService(List<PaymentService> paymentServices);

  Completable selectService(PaymentService selectedPaymentService);
}
