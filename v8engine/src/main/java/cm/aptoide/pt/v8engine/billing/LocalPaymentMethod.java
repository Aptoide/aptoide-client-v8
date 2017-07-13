package cm.aptoide.pt.v8engine.billing;

import rx.Completable;

public interface LocalPaymentMethod extends PaymentMethod {

  public Completable processLocal(Product product, String localMetadata);
}
