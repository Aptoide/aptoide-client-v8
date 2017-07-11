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
import cm.aptoide.pt.v8engine.view.ActivityView;

public class PaymentActivity extends ActivityView {

  public static Intent getIntent(Context context, long appId, String storeName, boolean sponsored) {
    final Intent intent = new Intent(context, PaymentActivity.class);
    intent.putExtras(ProductProvider.createBundle(appId, storeName, sponsored));
    return intent;
  }

  public static Intent getIntent(Context context, int apiVersion, String packageName, String sku,
      String type, String developerPayload) {
    final Intent intent = new Intent(context, PaymentActivity.class);
    intent.putExtras(
        ProductProvider.createBundle(apiVersion, packageName, type, sku, developerPayload));
    return intent;
  }

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.empty_frame);

    if (savedInstanceState == null) {
      getFragmentNavigator().navigateToWithoutBackSave(
          PaymentFragment.create(getIntent().getExtras()));
    }
  }
}
