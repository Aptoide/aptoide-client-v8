package cm.aptoide.pt.billing.view.boacompra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.billing.view.PaymentActivity;
import cm.aptoide.pt.billing.view.WebViewFragment;
import cm.aptoide.pt.navigator.ActivityResultNavigator;

public class BoaCompraFragment extends WebViewFragment {

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;

  public static Fragment create(Bundle bundle) {
    final BoaCompraFragment fragment = new BoaCompraFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling();
    billingAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    billingNavigator = ((ActivityResultNavigator) getContext()).getBillingNavigator();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachPresenter(new BoaCompraPresenter(this, billing, billingAnalytics, billingNavigator,
        getArguments().getString(PaymentActivity.EXTRA_APPLICATION_ID),
        getArguments().getString(PaymentActivity.EXTRA_PRODUCT_ID),
        getArguments().getString(PaymentActivity.EXTRA_DEVELOPER_PAYLOAD),
        getArguments().getString(PaymentActivity.EXTRA_PAYMENT_METHOD_NAME)), savedInstanceState);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}