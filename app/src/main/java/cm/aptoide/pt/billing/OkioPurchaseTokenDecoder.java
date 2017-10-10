package cm.aptoide.pt.billing;

import okio.ByteString;

public class OkioPurchaseTokenDecoder implements PurchaseTokenDecoder {

  @Override public long decode(String purchaseToken) {
    return Long.valueOf(ByteString.decodeBase64(purchaseToken).utf8());
  }
}