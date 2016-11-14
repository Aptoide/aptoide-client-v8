/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;


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

  public static PaymentRepository getPaymentRepository(Context context) {
    final NetworkOperatorManager operatorManager = new NetworkOperatorManager(
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    final ProductFactory productFactory = new ProductFactory();

    return new PaymentRepository(new AppRepository(operatorManager, productFactory),
        new InAppBillingRepository(operatorManager, productFactory), operatorManager,
        productFactory, new PurchaseFactory(new InAppBillingSerializer()), new PaymentFactory(),
        AccessorFactory.getAccessorFor(PaymentConfirmation.class));
  }
}
