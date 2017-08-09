/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.sync.BillingSyncManager;
import cm.aptoide.pt.v8engine.billing.view.braintree.BraintreeActivity;

public class PaymentActivity extends BraintreeActivity {

  public static final String EXTRA_DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.DEVELOPER_PAYLOAD";
  public static final String EXTRA_PRODUCT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PRODUCT_ID";
  public static final String EXTRA_APPLICATION_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.APPLICATION_ID";
  public static final String EXTRA_PAYMENT_METHOD_NAME =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_METHOD_NAME";

  private BillingSyncManager syncManager;

  public static Intent getIntent(Context context, String productId, String sellerId,
      String developerPayload) {
    final Intent intent = new Intent(context, PaymentActivity.class);
    intent.putExtra(EXTRA_PRODUCT_ID, productId);
    intent.putExtra(EXTRA_APPLICATION_ID, sellerId);
    intent.putExtra(EXTRA_DEVELOPER_PAYLOAD, developerPayload);
    return intent;
  }

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.empty_frame);

    if (savedInstanceState == null) {
      getFragmentNavigator().navigateToWithoutBackSave(
          PaymentFragment.create(getIntent().getExtras()));
    }

    syncManager = ((V8Engine) getApplication()).getBillingSyncManager();
  }

  @Override protected void onDestroy() {
    syncManager.cancelAll();
    super.onDestroy();
  }
}
