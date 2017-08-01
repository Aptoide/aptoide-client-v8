package cm.aptoide.pt.v8engine.billing.view.mol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.billing.view.ProductProvider;
import cm.aptoide.pt.v8engine.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.v8engine.billing.view.WebViewFragment;

public class MolFragment extends WebViewFragment {

  private static final String EXTRA_PAYMENT_METHOD_ID =
      "cm.aptoide.pt.v8engine.billing.view.extra.PAYMENT_METHOD_ID";
  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private ProductProvider productProvider;
  private int paymentMethodId;

  public static Fragment create(Bundle bundle, int paymentMethodId) {
    final MolFragment fragment = new MolFragment();
    bundle.putInt(EXTRA_PAYMENT_METHOD_ID, paymentMethodId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    billingAnalytics = ((V8Engine) getContext().getApplicationContext()).getBillingAnalytics();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
    paymentMethodId = getArguments().getInt(EXTRA_PAYMENT_METHOD_ID);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment_web_view, container, false);
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachPresenter(new MolPresenter(this, billing, billingAnalytics, productProvider,
        new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator(), getFragmentNavigator()), paymentMethodId), savedInstanceState);
  }
}