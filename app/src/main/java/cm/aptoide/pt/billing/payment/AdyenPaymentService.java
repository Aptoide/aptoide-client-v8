package cm.aptoide.pt.billing.payment;

import rx.Single;

public class AdyenPaymentService extends PaymentService {

  private final Adyen adyen;

  public AdyenPaymentService(String id, String type, String name, String description, String icon,
      Adyen adyen) {
    super(id, type, name, description, icon);
    this.adyen = adyen;
  }

  public Single<String> getToken() {
    return adyen.createPaymentRequest();
  }
}
