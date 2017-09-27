package cm.aptoide.pt.billing.view.paypal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.billing.view.PaymentActivity;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PayPalFragment extends PermissionServiceFragment implements PayPalView {

  private ProgressBar progressBar;
  private RxAlertDialog unknownErrorDialog;
  private RxAlertDialog networkErrorDialog;

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;

  public static Fragment create(Bundle bundle) {
    final PayPalFragment fragment = new PayPalFragment();
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
    progressBar = (ProgressBar) view.findViewById(R.id.fragment_paypal_progress_bar);

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(R.string.ok)
            .build();
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();

    attachPresenter(new PayPalPresenter(this, billing, billingAnalytics, billingNavigator,
        AndroidSchedulers.mainThread(),
        getArguments().getString(PaymentActivity.EXTRA_APPLICATION_ID),
        getArguments().getString(PaymentActivity.EXTRA_PRODUCT_ID),
        getArguments().getString(PaymentActivity.EXTRA_DEVELOPER_PAYLOAD),
        getArguments().getString(PaymentActivity.EXTRA_PAYMENT_METHOD_NAME)), savedInstanceState);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_paypal, container, false);
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
