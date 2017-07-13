package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;

public class BoaCompraFragment extends WebViewFragment implements WebView {

  private static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_ID";

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingSyncScheduler billingSyncScheduler;
  private ProductProvider productProvider;
  private int paymentId;

  public static Fragment create(Bundle bundle, int paymentId) {
    final BoaCompraFragment fragment = new BoaCompraFragment();
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    paymentId = getArguments().getInt(BoaCompraFragment.EXTRA_PAYMENT_ID);
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    billingAnalytics = ((V8Engine) getContext().getApplicationContext()).getBillingAnalytics();
    billingSyncScheduler =
        ((V8Engine) getContext().getApplicationContext()).getBillingSyncScheduler();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachPresenter(new BoaCompraPresenter(this, billing, billingAnalytics, billingSyncScheduler,
        productProvider,
        new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator(), getFragmentNavigator()), paymentId), savedInstanceState);
  }
}