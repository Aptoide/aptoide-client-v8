package cm.aptoide.pt.billing;

import okio.ByteString;

public class Base64PurchaseTokenDecoder implements PurchaseTokenDecoder {

  @Override public String decode(String purchaseToken) {
    return ByteString.decodeBase64(purchaseToken).utf8();
  }
}