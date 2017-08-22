package cm.aptoide.pt.v8engine.billing.exception;

public class PurchaseNotFoundException extends BillingException {

  public PurchaseNotFoundException(String message) {
    super(message);
  }

  public PurchaseNotFoundException() {
    super();
  }
}
