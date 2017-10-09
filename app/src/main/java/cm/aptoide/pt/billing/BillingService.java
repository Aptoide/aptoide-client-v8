package cm.aptoide.pt.billing;

import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentService>> getPaymentServices();

  Single<Merchant> getMerchant(String merchantName);

  Completable deletePurchase(long purchaseId);

  Single<List<Purchase>> getPurchases(String merchantName);

  Single<Purchase> getPurchase(long purchaseId);

  Single<List<Product>> getProducts(String merchantName, List<String> skus);

  Single<Product> getProduct(String merchantName, String sku);
}