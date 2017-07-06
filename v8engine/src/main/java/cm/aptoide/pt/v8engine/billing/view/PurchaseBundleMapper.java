/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.billing.product.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.product.PaidAppPurchase;

public class PurchaseBundleMapper {

  private static final String APK_PATH = "APK_PATH";
  private final PaymentThrowableCodeMapper throwableCodeMapper;

  public PurchaseBundleMapper(PaymentThrowableCodeMapper throwableCodeMapper) {
    this.throwableCodeMapper = throwableCodeMapper;
  }

  public Bundle map(Purchase purchase) {

    final Bundle intent = new Bundle();

    if (purchase instanceof InAppPurchase) {
      intent.putString(InAppBillingBinder.INAPP_PURCHASE_DATA,
          ((InAppPurchase) purchase).getSignatureData());
      intent.putInt(InAppBillingBinder.RESPONSE_CODE, InAppBillingBinder.RESULT_OK);

      if (((InAppPurchase) purchase).getSignature() != null) {
        intent.putString(InAppBillingBinder.INAPP_DATA_SIGNATURE,
            ((InAppPurchase) purchase).getSignature());
      }
    } else if (purchase instanceof PaidAppPurchase) {
      intent.putInt(InAppBillingBinder.RESPONSE_CODE, InAppBillingBinder.RESULT_OK);
      intent.putString(APK_PATH, ((PaidAppPurchase) purchase).getApkPath());
    } else {
      intent.putInt(InAppBillingBinder.RESPONSE_CODE, throwableCodeMapper.map(
          new IllegalArgumentException(
              "Cannot convert purchase. Only paid app and in app purchases supported.")));
    }
    return intent;
  }

  public Purchase map(Bundle intent, int resultCode) throws Throwable {

    if (intent != null) {
      if (resultCode == Activity.RESULT_OK) {

        if (intent.containsKey(APK_PATH)) {
          return new PaidAppPurchase(intent.getString(APK_PATH));
        }

        throw new IllegalArgumentException("Intent does not contain paid app apk path");
      } else if (resultCode == Activity.RESULT_CANCELED) {

        if (intent.containsKey(InAppBillingBinder.RESPONSE_CODE)) {
          throw throwableCodeMapper.map(intent.getInt(InAppBillingBinder.RESPONSE_CODE, -1));
        }
      }

      throw new IllegalArgumentException("Invalid result code " + resultCode);
    }

    throw throwableCodeMapper.map(InAppBillingBinder.RESULT_USER_CANCELLED);
  }

  public Bundle map(Throwable throwable) {
    final Bundle bundle = new Bundle();
    bundle.putInt(InAppBillingBinder.RESPONSE_CODE, throwableCodeMapper.map(throwable));
    return bundle;
  }

  public Bundle mapCancellation() {
    final Bundle bundle = new Bundle();
    bundle.putInt(InAppBillingBinder.RESPONSE_CODE, InAppBillingBinder.RESULT_USER_CANCELLED);
    return bundle;
  }
}
