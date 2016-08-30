/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;

public class InAppBillingService extends Service {

    private AptoideInAppBillingService.Stub billingBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        final NetworkOperatorManager operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        final InAppBillingSerializer serializer = new InAppBillingSerializer();
        billingBinder = new BillingBinder(this, new InAppBillingRepository(operatorManager, new ProductFactory()), serializer, new ErrorCodeFactory(),
                new PurchaseErrorCodeFactory(), new ProductFactory());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return billingBinder;
    }

}