/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.billing.view;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.billing.external.ExternalBillingBinder;
import cm.aptoide.pt.billing.purchase.InAppPurchase;
import cm.aptoide.pt.billing.purchase.PaidAppPurchase;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;

public class PurchaseBundleMapper {

  private static final String APK_PATH = "APK_PATH";
  private static final String PRODUCT_ID = "PRODUCT_ID";
  private static final String TRANSACTION_ID = "TRANSACTION_ID";
  private static final String STATUS = "STATUS";
  private final PaymentThrowableCodeMapper throwableCodeMapper;
  private final PurchaseFactory purchaseFactory;

  public PurchaseBundleMapper(PaymentThrowableCodeMapper throwableCodeMapper,
      PurchaseFactory purchaseFactory) {
    this.throwableCodeMapper = throwableCodeMapper;
    this.purchaseFactory = purchaseFactory;
  }

  public Bundle map(Purchase purchase) {

    final Bundle intent = new Bundle();

    if (purchase instanceof InAppPurchase) {
      intent.putString(ExternalBillingBinder.INAPP_PURCHASE_DATA,
          ((InAppPurchase) purchase).getSignatureData());
      intent.putInt(ExternalBillingBinder.RESPONSE_CODE, ExternalBillingBinder.RESULT_OK);

      if (((InAppPurchase) purchase).getSignature() != null) {
        intent.putString(ExternalBillingBinder.INAPP_DATA_SIGNATURE,
            ((InAppPurchase) purchase).getSignature());
      }
    } else if (purchase instanceof PaidAppPurchase) {
      intent.putInt(ExternalBillingBinder.RESPONSE_CODE, ExternalBillingBinder.RESULT_OK);
      intent.putString(APK_PATH, ((PaidAppPurchase) purchase).getApkPath());
      intent.putString(PRODUCT_ID, purchase.getProductId());
      intent.putString(TRANSACTION_ID, purchase.getTransactionId());
      intent.putSerializable(STATUS, purchase.getStatus());
    } else {
      intent.putInt(ExternalBillingBinder.RESPONSE_CODE, throwableCodeMapper.map(
          new IllegalArgumentException(
              "Cannot convert purchase. Only paid app and in app purchases supported.")));
    }
    return intent;
  }

  public Purchase map(int resultCode, Bundle data) throws Throwable {

    if (resultCode == Activity.RESULT_OK) {
      if (data != null) {

        if (data.containsKey(APK_PATH) && data.containsKey(PRODUCT_ID)) {
          return purchaseFactory.create(data.getString(PRODUCT_ID), null, null,
              (Purchase.Status) data.getSerializable(STATUS), null, PurchaseFactory.PAID_APP,
              data.getString(APK_PATH), data.getString(TRANSACTION_ID));
        }

        throw new IllegalArgumentException("Intent does not contain paid app information");
      }

      throw new IllegalArgumentException("No purchase provided in result intent.");
    } else if (resultCode == Activity.RESULT_CANCELED) {

      if (data != null && data.containsKey(ExternalBillingBinder.RESPONSE_CODE)) {
        throw throwableCodeMapper.map(data.getInt(ExternalBillingBinder.RESPONSE_CODE, -1));
      }
    }

    throw throwableCodeMapper.map(ExternalBillingBinder.RESULT_ERROR);
  }

  public Bundle map(Throwable throwable) {
    final Bundle bundle = new Bundle();
    bundle.putInt(ExternalBillingBinder.RESPONSE_CODE, throwableCodeMapper.map(throwable));
    return bundle;
  }

  public Bundle mapCancellation() {
    final Bundle bundle = new Bundle();
    bundle.putInt(ExternalBillingBinder.RESPONSE_CODE, ExternalBillingBinder.RESULT_USER_CANCELLED);
    return bundle;
  }
}
