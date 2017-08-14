package cm.aptoide.pt.v8engine.billing.external;

import cm.aptoide.pt.v8engine.billing.Purchase;
import java.util.List;
import rx.Completable;
import rx.Single;

public interface ExternalBillingService {

  Single<List<Purchase>> getInAppPurchases(int apiVersion, String packageName, String type);

  Completable consumeInAppPurchase(int apiVersion, String packageName, String purchaseToken);
}
