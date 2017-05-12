package cm.aptoide.pt.v8engine.billing.purchase;

import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import java.io.IOException;
import rx.Completable;

public class InAppPurchase implements Purchase {

  private final InAppBillingPurchasesResponse.InAppBillingPurchase purchase;
  private final String purchaseSignature;
  private final InAppBillingSerializer serializer;
  private final InAppBillingRepository repository;
  private final int apiVersion;

  public InAppPurchase(InAppBillingPurchasesResponse.InAppBillingPurchase purchase,
      String purchaseSignature, InAppBillingSerializer serializer,
      InAppBillingRepository repository, int apiVersion) {
    this.purchase = purchase;
    this.purchaseSignature = purchaseSignature;
    this.serializer = serializer;
    this.repository = repository;
    this.apiVersion = apiVersion;
  }

  @Override public String getData() throws IOException {
    return serializer.serializePurchase(purchase);
  }

  @Override public String getSignature() {
    return purchaseSignature;
  }

  @Override public Completable consume() {
    return repository.deleteInAppPurchase(apiVersion, purchase.getPackageName(),
        purchase.getPurchaseToken());
  }
}
