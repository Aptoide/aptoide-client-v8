/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.content.Intent;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.billing.purchase.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.purchase.PaidAppPurchase;

public class PurchaseIntentMapper {

  private static final String APK_PATH = "APK_PATH";
  private final PaymentThrowableCodeMapper throwableCodeMapper;

  public PurchaseIntentMapper(PaymentThrowableCodeMapper throwableCodeMapper) {
    this.throwableCodeMapper = throwableCodeMapper;
  }

  public Intent map(Purchase purchase) {

    final Intent intent = new Intent();

    if (purchase instanceof InAppPurchase) {
      intent.putExtra(InAppBillingBinder.INAPP_PURCHASE_DATA,
          ((InAppPurchase) purchase).getSignatureData());
      intent.putExtra(InAppBillingBinder.RESPONSE_CODE, InAppBillingBinder.RESULT_OK);

      if (((InAppPurchase) purchase).getSignature() != null) {
        intent.putExtra(InAppBillingBinder.INAPP_DATA_SIGNATURE,
            ((InAppPurchase) purchase).getSignature());
      }
    } else if (purchase instanceof PaidAppPurchase) {
      intent.putExtra(InAppBillingBinder.RESPONSE_CODE, InAppBillingBinder.RESULT_OK);
      intent.putExtra(APK_PATH, ((PaidAppPurchase) purchase).getApkPath());
    } else {
      intent.putExtra(InAppBillingBinder.RESPONSE_CODE, throwableCodeMapper.map(
          new IllegalArgumentException(
              "Cannot convert purchase. Only paid app and in app purchases supported.")));
    }
    return intent;
  }

  public Purchase map(Intent intent, int resultCode) throws Throwable {

    if (intent != null) {
      if (resultCode == Activity.RESULT_OK) {

        if (intent.hasExtra(APK_PATH)) {
          return new PaidAppPurchase(intent.getStringExtra(APK_PATH));
        }

        throw new IllegalArgumentException("Intent does not contain paid app apk path");
      } else if (resultCode == Activity.RESULT_CANCELED) {

        if (intent.hasExtra(InAppBillingBinder.RESPONSE_CODE)) {
          throw throwableCodeMapper.map(intent.getIntExtra(InAppBillingBinder.RESPONSE_CODE, -1));
        }
      }

      throw new IllegalArgumentException("Invalid result code " + resultCode);
    }

    throw throwableCodeMapper.map(InAppBillingBinder.RESULT_USER_CANCELLED);
  }

  public Intent map(Throwable throwable) {
    return new Intent().putExtra(InAppBillingBinder.RESPONSE_CODE,
        throwableCodeMapper.map(throwable));
  }

  public Intent mapCancellation() {
    return new Intent().putExtra(InAppBillingBinder.RESPONSE_CODE,
        InAppBillingBinder.RESULT_USER_CANCELLED);
  }
}
