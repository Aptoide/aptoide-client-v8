package cm.aptoide.pt.billing.view.adyen;

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
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.adyen.core.PaymentRequest;
import com.adyen.ui.views.CVCDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AdyenAuthorizationFragment extends PermissionServiceFragment
    implements AdyenAuthorizationView {

  private Billing billing;
  private ProgressBar progressBar;
  private RxAlertDialog networkErrorDialog;
  private BillingNavigator navigator;
  private BillingAnalytics analytics;
  private Adyen adyen;
  private PublishRelay<Void> backButton;
  private ClickHandler clickHandler;
  private CVCDialog cvcDialog;

  public static Fragment create(Bundle bundle) {
    final AdyenAuthorizationFragment fragment = new AdyenAuthorizationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling(
        getArguments().getString(BillingActivity.EXTRA_MERCHANT_NAME));
    navigator = ((ActivityResultNavigator) getActivity()).getBillingNavigator();
    analytics = ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    adyen = ((AptoideApplication) getContext().getApplicationContext()).getAdyen();
    backButton = PublishRelay.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    progressBar = (ProgressBar) view.findViewById(R.id.fragment_adyen_authorization_progress_bar);

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(R.string.ok)
            .build();

    clickHandler = new ClickHandler() {
      @Override public boolean handle() {
        backButton.call(null);
        return true;
      }
    };
    registerClickHandler(clickHandler);

    attachPresenter(
        new AdyenAuthorizationPresenter(this, getArguments().getString(BillingActivity.EXTRA_SKU),
            billing, navigator, analytics,
            getArguments().getString(BillingActivity.EXTRA_SERVICE_NAME), adyen,
            AndroidSchedulers.mainThread()));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_adyen_authorization, container, false);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(clickHandler);
    if (cvcDialog != null) {
      cvcDialog.dismiss();
    }
    cvcDialog = null;
    progressBar = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    super.onDestroyView();
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<Void> errorDismisses() {
    return networkErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public Observable<Void> backButtonEvent() {
    return backButton;
  }

  @Override public void showCvvView(PaymentRequest request) {
    if (cvcDialog != null && cvcDialog.isShowing()) {
      cvcDialog.dismiss();
    }
    cvcDialog = new CVCDialog(getActivity(), request.getAmount(), request.getPaymentMethod(),
        () -> backButton.call(null));
    cvcDialog.show();
  }
}
