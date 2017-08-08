package cm.aptoide.pt.v8engine.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentMethod>> getPaymentMethods(Product product);

  Completable getBilling(String applicationId, String type);

  Completable deletePurchase(String applicationId, String purchaseToken);

  Single<List<Purchase>> getPurchases(String applicationId);

  Single<List<Product>> getProducts(String applicationId, List<String> productIds);

  Single<Purchase> getPurchase(Product product);

  Single<Product> getProduct(String applicationId, String productId);

}
