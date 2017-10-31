package cm.aptoide.pt.billing.view.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.billing.view.PaymentActivity;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public class WebAuthorizationFragment extends PermissionServiceFragment
    implements WebAuthorizationView {

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;

  private WebView webView;
  private View indeterminateProgressBar;
  private RxAlertDialog unknownErrorDialog;
  private PublishRelay<Void> urlLoadErrorSubject;
  private PublishRelay<Void> redirectUrlSubject;
  private PublishRelay<Void> backButtonSelectionSubject;
  private ClickHandler clickHandler;
  private ProgressBar determinateProgressBar;

  public static Fragment create(Bundle bundle) {
    final WebAuthorizationFragment fragment = new WebAuthorizationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling(
        getArguments().getString(PaymentActivity.EXTRA_MERCHANT_NAME));
    billingAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    billingNavigator = ((ActivityResultNavigator) getContext()).getBillingNavigator();
    redirectUrlSubject = PublishRelay.create();
    urlLoadErrorSubject = PublishRelay.create();
    backButtonSelectionSubject = PublishRelay.create();
  }

  @Override @SuppressLint("SetJavaScriptEnabled")
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    webView = (WebView) view.findViewById(R.id.fragment_web_view);
    webView.getSettings()
        .setJavaScriptEnabled(true);
    webView.setWebChromeClient(new WebChromeClient());
    indeterminateProgressBar = view.findViewById(R.id.fragment_web_view_indeterminate_progress_bar);
    determinateProgressBar =
        (ProgressBar) view.findViewById(R.id.fragment_web_view_determinate_progress_bar);
    determinateProgressBar.setMax(100);
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();
    clickHandler = () -> {
      backButtonSelectionSubject.call(null);
      return false;
    };
    registerClickHandler(clickHandler);

    attachPresenter(new WebAuthorizationPresenter(this, billing, billingAnalytics, billingNavigator,
        getArguments().getString(PaymentActivity.EXTRA_SERVICE_NAME),
        getArguments().getString(PaymentActivity.EXTRA_SKU)));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_web_view, container, false);
  }

  @Override public void onDestroyView() {
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.setWebChromeClient(null);
    webView.destroy();
    webView = null;
    unknownErrorDialog.dismiss();
    unknownErrorDialog = null;
    unregisterClickHandler(clickHandler);
    clickHandler = null;
    super.onDestroyView();
  }

  @Override public void showLoading() {
    indeterminateProgressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    indeterminateProgressBar.setVisibility(View.GONE);
  }

  @Override public void loadWebsite(String mainUrl, String redirectUrl) {
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public void onProgressChanged(WebView view, int progress) {
        determinateProgressBar.setProgress(progress);
      }
    });
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.equals(redirectUrl)) {
          redirectUrlSubject.call(null);
        }
      }

      @Override public void onReceivedError(WebView view, int errorCode, String description,
          String failingUrl) {
        urlLoadErrorSubject.call(null);
      }
    });
    webView.loadUrl(mainUrl);
  }

  @Override public Observable<Void> redirectUrlEvent() {
    return redirectUrlSubject;
  }

  @Override public Observable<Void> loadUrlErrorEvent() {
    return urlLoadErrorSubject;
  }

  @Override public Observable<Void> backButtonEvent() {
    return backButtonSelectionSubject;
  }

  @Override public void showError() {
    unknownErrorDialog.show();
  }

  @Override public Observable<Void> errorDismissedEvent() {
    return unknownErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }
}