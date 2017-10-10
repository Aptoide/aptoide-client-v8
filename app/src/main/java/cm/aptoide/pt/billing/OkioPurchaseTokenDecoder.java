package cm.aptoide.pt.billing;

import okio.ByteString;

public class OkioPurchaseTokenDecoder implements PurchaseTokenDecoder {

  @Override public long decode(String purchaseToken) {
    return ByteString.decodeBase64(purchaseToken)
        .asByteBuffer()
        .getLong();
  }
}