package cm.aptoide.pt.billing;

import rx.Single;

public class TokenPaymentService extends PaymentService {

  private final Adyen adyen;

  public TokenPaymentService(String id, String type, String name, String description, String icon,
      Adyen adyen) {
    super(id, type, name, description, icon);
    this.adyen = adyen;
  }

  public Single<String> generateToken() {
    adyen.generateToken();
    return adyen.getToken()
        .first()
        .toSingle();
  }
}
