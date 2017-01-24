/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import cm.aptoide.pt.v8engine.repository.sync.SyncDataConverter;

/**
 * Created by sithengineer on 02/09/16.
 */
public final class RepositoryFactory {

  public static ScheduledDownloadRepository getScheduledDownloadRepository() {
    return new ScheduledDownloadRepository(AccessorFactory.getAccessorFor(Scheduled.class));
  }

  public static RollbackRepository getRollbackRepository() {
    return new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class));
  }

  public static UpdateRepository getUpdateRepository() {
    return new UpdateRepository(AccessorFactory.getAccessorFor(Update.class),
        AccessorFactory.getAccessorFor(Store.class));
  }

  public static InstalledRepository getInstalledRepository() {
    return new InstalledRepository(AccessorFactory.getAccessorFor(Installed.class));
  }

  public static StoreRepository getStoreRepository() {
    return new StoreRepository(AccessorFactory.getAccessorFor(Store.class));
  }

  public static ProductRepository getProductRepository(Context context, AptoideProduct product) {
    final PurchaseFactory purchaseFactory = new PurchaseFactory(new InAppBillingSerializer());
    final PaymentFactory paymentFactory = new PaymentFactory(context);
    final NetworkOperatorManager operatorManager = getNetworkOperatorManager(context);
    if (product instanceof InAppBillingProduct) {
      return new InAppBillingProductRepository(new InAppBillingRepository(operatorManager,
          AccessorFactory.getAccessorFor(PaymentConfirmation.class)), purchaseFactory,
          paymentFactory);
    } else {
      return new PaidAppProductRepository(new AppRepository(operatorManager), purchaseFactory,
          paymentFactory);
    }
  }

  public static PaymentConfirmationRepository getPaymentConfirmationRepository(Context context,
      Product product) {
    if (product instanceof InAppBillingProduct) {
      return new InAppPaymentConfirmationRepository(getNetworkOperatorManager(context),
          AccessorFactory.getAccessorFor(PaymentConfirmation.class), getBackgroundSync(context),
          new PaymentConfirmationFactory(), (InAppBillingProduct) product);
    } else {
      return new PaidAppPaymentConfirmationRepository(getNetworkOperatorManager(context),
          AccessorFactory.getAccessorFor(PaymentConfirmation.class), getBackgroundSync(context),
          new PaymentConfirmationFactory(), (PaidAppProduct) product);
    }
  }

  public static PaymentAuthorizationRepository getPaymentAuthorizationRepository(Context context) {
    return new PaymentAuthorizationRepository(
        AccessorFactory.getAccessorFor(PaymentAuthorization.class), getBackgroundSync(context),
        new PaymentAuthorizationFactory(context));
  }

  public static InAppBillingRepository getInAppBillingRepository(Context context) {
    return new InAppBillingRepository(getNetworkOperatorManager(context),
        AccessorFactory.getAccessorFor(PaymentConfirmation.class));
  }

  private static SyncAdapterBackgroundSync getBackgroundSync(Context context) {
    return new SyncAdapterBackgroundSync(Application.getConfiguration(),
        (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE),
        new SyncDataConverter());
  }

  private static NetworkOperatorManager getNetworkOperatorManager(Context context) {
    return new NetworkOperatorManager(
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
  }
}
