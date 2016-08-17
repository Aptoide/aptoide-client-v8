/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.iab.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;

public class BillingService extends Service {

    private IOpenInAppBillingService.Stub billingBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        final NetworkOperatorManager operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        billingBinder = new InAppBillingBinder(this, new InAppBillingRepository(operatorManager, new ProductFactory(), new PaymentFactory()), new InAppBillingSerializer(),
                operatorManager);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return billingBinder;
    }

}