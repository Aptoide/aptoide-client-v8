package cm.aptoide.pt.v8engine.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentMethod>> getPaymentMethods(Product product);

  Completable getBilling(int apiVersion, String packageName, String type);

  Completable deletePurchase(int apiVersion, String packageName, String purchaseToken);

  Single<List<Purchase>> getPurchases(int apiVersion, String packageName, String type);

  Single<List<Product>> getProducts(int apiVersion, String packageName, List<String> skus,
      String type);

  Single<Purchase> getPurchase(Product product);

  Single<Product> getProduct(int apiVersion, String packageName, String sku, String type,
      String developerPayload);

  Single<Product> getProduct(long appId, boolean sponsored, String storeName);
}
