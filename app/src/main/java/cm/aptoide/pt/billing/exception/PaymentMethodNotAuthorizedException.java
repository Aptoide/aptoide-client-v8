package cm.aptoide.pt.billing.exception;

public class PaymentMethodNotAuthorizedException extends BillingException {

  public PaymentMethodNotAuthorizedException(String message) {
    super(message);
  }
}
