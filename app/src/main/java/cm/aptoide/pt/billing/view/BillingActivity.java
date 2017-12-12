/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.billing.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.view.payment.PaymentFragment;
import cm.aptoide.pt.view.BackButtonActivity;

public class BillingActivity extends BackButtonActivity {

  public static final String EXTRA_DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.view.payment.intent.extra.DEVELOPER_PAYLOAD";
  public static final String EXTRA_SKU = "cm.aptoide.pt.view.payment.intent.extra.SKU";
  public static final String EXTRA_MERCHANT_NAME =
      "cm.aptoide.pt.view.payment.intent.extra.MERCHANT_NAME";
  public static final String EXTRA_SERVICE_NAME =
      "cm.aptoide.pt.view.payment.intent.extra.SERVICE_NAME";

  private Billing billing;

  public static Intent getIntent(Context context, String sku, String merchantName,
      String developerPayload) {
    final Intent intent = new Intent(context, BillingActivity.class);
    intent.putExtra(EXTRA_SKU, sku);
    intent.putExtra(EXTRA_MERCHANT_NAME, merchantName);
    intent.putExtra(EXTRA_DEVELOPER_PAYLOAD, developerPayload);
    return intent;
  }

  public static Intent getIntent(Context context, long appId, String merchantName) {
    final Intent intent = new Intent(context, BillingActivity.class);
    intent.putExtra(EXTRA_SKU, String.valueOf(appId));
    intent.putExtra(EXTRA_MERCHANT_NAME, merchantName);
    return intent;
  }

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.empty_frame);

    if (savedInstanceState == null) {
      getFragmentNavigator().navigateToWithoutBackSave(
          PaymentFragment.create(getIntent().getExtras()), true);
    }

    billing = ((AptoideApplication) getApplication()).getBilling(
        getIntent().getStringExtra(EXTRA_MERCHANT_NAME));
  }

  @Override protected void onDestroy() {
    billing.stopSync();
    super.onDestroy();
  }
}
