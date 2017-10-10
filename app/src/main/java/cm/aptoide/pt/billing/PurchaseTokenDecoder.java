package cm.aptoide.pt.billing;

public interface PurchaseTokenDecoder {

  long decode(String purchaseToken);
}
