package cm.aptoide.pt.v8engine.billing.view.paypal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.billing.view.ProductProvider;
import cm.aptoide.pt.v8engine.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PayPalFragment extends PermissionServiceFragment implements PayPalView {

  private static final String EXTRA_PAYMENT_METHOD_ID =
      "cm.aptoide.pt.v8engine.billing.view.extra.PAYMENT_METHOD_ID";
  private ProgressBar progressBar;
  private RxAlertDialog unknownErrorDialog;
  private RxAlertDialog networkErrorDialog;

  private Billing billing;
  private ProductProvider productProvider;
  private BillingAnalytics billingAnalytics;
  private int paymentMethodId;

  public static Fragment create(Bundle bundle, int paymentMethodId) {
    final PayPalFragment fragment = new PayPalFragment();
    bundle.putInt(EXTRA_PAYMENT_METHOD_ID, paymentMethodId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
    billingAnalytics = ((V8Engine) getContext().getApplicationContext()).getBillingAnalytics();
    paymentMethodId = getArguments().getInt(EXTRA_PAYMENT_METHOD_ID);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_paypal, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    progressBar = (ProgressBar) view.findViewById(R.id.fragment_paypal_progress_bar);

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(R.string.ok)
            .build();
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();

    attachPresenter(new PayPalPresenter(this, billing, productProvider, billingAnalytics,
        new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator(), getFragmentNavigator()), AndroidSchedulers.mainThread(),
        paymentMethodId), savedInstanceState);
  }

  @Override public void onDestroyView() {
    progressBar = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    unknownErrorDialog.dismiss();
    unknownErrorDialog = null;
    super.onDestroyView();
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public void showUnknownError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      unknownErrorDialog.show();
    }
  }

  @Override public Observable<Void> errorDismisses() {
    return Observable.merge(networkErrorDialog.dismisses(), unknownErrorDialog.dismisses())
        .map(dialogInterface -> null);
  }
}
