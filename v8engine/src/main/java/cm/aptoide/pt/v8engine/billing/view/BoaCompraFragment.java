package cm.aptoide.pt.v8engine.billing.view;

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
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public class BoaCompraFragment extends PermissionServiceFragment implements BoaCompraView {

  public static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_ID";

  private WebView webView;
  private View progressBarContainer;
  private RxAlertDialog unknownErrorDialog;
  private PublishRelay<Void> mainUrlSubject;
  private PublishRelay<Void> redirectUrlSubject;
  private PublishRelay<Void> backButtonSelectionSubject;
  private ClickHandler clickHandler;
  private int paymentId;

  public static Fragment create(Bundle bundle, int paymentId) {
    final BoaCompraFragment fragment = new BoaCompraFragment();
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mainUrlSubject = PublishRelay.create();
    redirectUrlSubject = PublishRelay.create();
    backButtonSelectionSubject = PublishRelay.create();
    paymentId = getArguments().getInt(EXTRA_PAYMENT_ID);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_boa_compra, container, false);
  }

  @SuppressLint("SetJavaScriptEnabled") @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    webView = (WebView) view.findViewById(R.id.activity_boa_compra_authorization_web_view);
    webView.getSettings()
        .setJavaScriptEnabled(true);
    webView.setWebChromeClient(new WebChromeClient());
    progressBarContainer = view.findViewById(R.id.activity_web_authorization_preogress_bar);
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();
    clickHandler = () -> {
      backButtonSelectionSubject.call(null);
      return false;
    };
    registerClickHandler(clickHandler);

    attachPresenter(
        new BoaCompraPresenter(this, ((V8Engine) getContext().getApplicationContext()).getBilling(),
            ((V8Engine) getContext().getApplicationContext()).getPaymentAnalytics(),
            ((V8Engine) getContext().getApplicationContext()).getPaymentSyncScheduler(),
            ProductProvider.fromBundle(
                ((V8Engine) getContext().getApplicationContext()).getBilling(), getArguments()),
            new PaymentNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
                getActivityNavigator(), getFragmentNavigator()), paymentId), savedInstanceState);
  }

  @Override public void onDestroyView() {
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.destroy();
    webView = null;
    unknownErrorDialog.dismiss();
    unknownErrorDialog = null;
    unregisterClickHandler(clickHandler);
    clickHandler = null;
    progressBarContainer = null;
    super.onDestroyView();
  }

  @Override public void showLoading() {
    progressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBarContainer.setVisibility(View.GONE);
  }

  @Override public void loadBoaCompraConsentWebsite(String mainUrl, String redirectUrl) {
    webView.setWebViewClient(new WebViewClient() {

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.equals(redirectUrl)) {
          redirectUrlSubject.call(null);
        }
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (url.equals(mainUrl)) {
          mainUrlSubject.call(null);
        }
      }
    });
    webView.loadUrl(mainUrl);
  }

  @Override public Observable<Void> backToStoreEvent() {
    return redirectUrlSubject;
  }

  @Override public Observable<Void> backButtonSelection() {
    return backButtonSelectionSubject;
  }

  @Override public Observable<Void> boaCompraConsentWebsiteLoaded() {
    return mainUrlSubject;
  }

  @Override public void showError() {
    unknownErrorDialog.show();
  }

  @Override public Observable<Void> errorDismissedEvent() {
    return unknownErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }
}
