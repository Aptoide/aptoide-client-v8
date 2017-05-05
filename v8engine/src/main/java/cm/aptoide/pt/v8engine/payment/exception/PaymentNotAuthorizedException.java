package cm.aptoide.pt.v8engine.payment.exception;

import rx.functions.Func0;

public class PaymentNotAuthorizedException extends PaymentException {

  public PaymentNotAuthorizedException(String message) {
    super(message);
  }
}
