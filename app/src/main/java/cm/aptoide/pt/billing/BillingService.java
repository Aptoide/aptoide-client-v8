package cm.aptoide.pt.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentMethod>> getPaymentMethods();

  Single<Merchant> getMerchant(String merchantName);

  Completable deletePurchase(String merchantName, String purchaseToken);

  Single<List<Purchase>> getPurchases(String merchantName);

  Single<Purchase> getPurchase(String merchantName, String purchaseToken);

  Single<List<Product>> getProducts(String merchantName, List<String> productIds);

  Single<Purchase> getPurchase(Product product);

  Single<Product> getProduct(String merchantName, String productId);
}
