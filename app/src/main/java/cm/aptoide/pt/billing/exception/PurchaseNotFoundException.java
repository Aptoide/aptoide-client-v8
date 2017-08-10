package cm.aptoide.pt.billing.exception;

public class PurchaseNotFoundException extends BillingException {

  public PurchaseNotFoundException(String message) {
    super(message);
  }

  public PurchaseNotFoundException() {
    super();
  }
}
