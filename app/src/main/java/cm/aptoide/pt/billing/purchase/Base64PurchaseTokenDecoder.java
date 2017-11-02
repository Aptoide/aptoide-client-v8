package cm.aptoide.pt.billing.purchase;

import cm.aptoide.pt.billing.PurchaseTokenDecoder;
import okio.ByteString;

public class Base64PurchaseTokenDecoder implements PurchaseTokenDecoder {

  @Override public String decode(String purchaseToken) {
    return ByteString.decodeBase64(purchaseToken)
        .utf8();
  }
}