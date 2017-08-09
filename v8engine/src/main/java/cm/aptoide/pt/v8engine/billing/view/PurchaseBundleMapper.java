/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.external.ExternalBillingBinder;
import cm.aptoide.pt.v8engine.billing.product.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.product.PaidAppPurchase;
import cm.aptoide.pt.v8engine.billing.product.SimplePurchase;

public class PurchaseBundleMapper {

  private static final String APK_PATH = "APK_PATH";
  private static final String PRODUCT_ID = "PRODUCT_ID";
  private final PaymentThrowableCodeMapper throwableCodeMapper;

  public PurchaseBundleMapper(PaymentThrowableCodeMapper throwableCodeMapper) {
    this.throwableCodeMapper = throwableCodeMapper;
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
          return new PaidAppPurchase(data.getString(APK_PATH), SimplePurchase.Status.COMPLETED,
              data.getString(PRODUCT_ID));
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
