package cm.aptoide.pt.v8engine.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentMethod>> getPaymentMethods(Product product);

  Completable getBilling(String sellerId, String type);

  Completable deletePurchase(String sellerId, String purchaseToken);

  Single<List<Purchase>> getPurchases(String sellerId);

  Single<Purchase> getPurchase(String sellerId, String purchaseToken);

  Single<List<Product>> getProducts(String sellerId, List<String> productIds);

  Single<Purchase> getPurchase(Product product);

  Single<Product> getProduct(String sellerId, String productId);
}
