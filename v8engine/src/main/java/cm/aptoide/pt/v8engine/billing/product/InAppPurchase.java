package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import rx.Completable;

public class InAppPurchase implements Purchase {

  private final String signature;
  private final InAppBillingRepository repository;
  private final int apiVersion;
  private final String signatureData;
  private final String packageName;
  private final String purchaseToken;
  private final String sku;

  public InAppPurchase(int apiVersion, String packageName, String purchaseToken, String signature,
      String signatureData, InAppBillingRepository repository, String sku) {
    this.signature = signature;
    this.repository = repository;
    this.apiVersion = apiVersion;
    this.signatureData = signatureData;
    this.packageName = packageName;
    this.purchaseToken = purchaseToken;
    this.sku = sku;
  }

  @Override public Completable consume() {
    return repository.deleteInAppPurchase(apiVersion, packageName, purchaseToken);
  }

  public String getSignature() {
    return signature;
  }

  public String getSignatureData() {
    return signatureData;
  }

  public String getSku() {
    return sku;
  }
}