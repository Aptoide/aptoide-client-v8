package cm.aptoide.pt.billing.view;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.PaymentMethod;
import cm.aptoide.pt.billing.PaymentMethodMapper;
import cm.aptoide.pt.billing.Purchase;
import cm.aptoide.pt.billing.view.login.PaymentLoginFragment;
import cm.aptoide.pt.billing.view.web.WebAuthorizationFragment;
import cm.aptoide.pt.billing.view.paypal.PayPalAuthorizationFragment;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import java.math.BigDecimal;
import rx.Observable;

public class BillingNavigator {

  private final PurchaseBundleMapper bundleMapper;
  private final ActivityNavigator activityNavigator;
  private final FragmentNavigator fragmentNavigator;
  private final String marketName;

  public BillingNavigator(PurchaseBundleMapper bundleMapper, ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator, String marketName) {
    this.bundleMapper = bundleMapper;
    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
    this.marketName = marketName;
  }

  public void navigateToCustomerAuthenticationForResult(int requestCode) {
    fragmentNavigator.navigateForResult(PaymentLoginFragment.newInstance(), requestCode, true);
  }

  public Observable<Boolean> customerAuthenticationResults(int requestCode) {
    return fragmentNavigator.results(requestCode)
        .map(result -> result.getResultCode() == Activity.RESULT_OK);
  }

  public void navigateToTransactionAuthorizationView(String merchantName, PaymentMethod service,
      String sku) {

    final Bundle bundle = getAuthorizationBundle(merchantName, sku, service.getName());

    switch (service.getId()) {
      case PaymentMethodMapper.PAYPAL:
        fragmentNavigator.navigateTo(PayPalAuthorizationFragment.create(bundle), true);
        break;
      case PaymentMethodMapper.MOL_POINTS:
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
        fragmentNavigator.navigateTo(WebAuthorizationFragment.create(bundle), true);
        break;
      case PaymentMethodMapper.SANDBOX:
      default:
        throw new IllegalArgumentException("Service "
            + service.getId()
            + " does not require authorization. Can not navigate to authorization view.");
    }
  }

  public void popView() {
    fragmentNavigator.popBackStack();
  }

  public void navigateToPayPalForResult(int requestCode, String currency, String description,
      double amount) {

    final Bundle bundle = new Bundle();
    bundle.putParcelable(PayPalService.EXTRA_PAYPAL_CONFIGURATION,
        new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
            .clientId(BuildConfig.PAYPAL_KEY)
            .merchantName(marketName));
    bundle.putParcelable(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,
        new PayPalPayment(new BigDecimal(amount), currency, description,
            PayPalPayment.PAYMENT_INTENT_SALE));

    activityNavigator.navigateForResult(com.paypal.android.sdk.payments.PaymentActivity.class,
        requestCode, bundle);
  }

  public Observable<PayPalResult> payPalResults(int requestCode) {
    return activityNavigator.results(requestCode)
        .map(result -> map(result));
  }

  public void popViewWithResult(Purchase purchase) {
    activityNavigator.navigateBackWithResult(Activity.RESULT_OK, bundleMapper.map(purchase));
  }

  public void popViewWithResult(Throwable throwable) {
    activityNavigator.navigateBackWithResult(Activity.RESULT_CANCELED, bundleMapper.map(throwable));
  }

  public void popViewWithResult() {
    activityNavigator.navigateBackWithResult(Activity.RESULT_CANCELED,
        bundleMapper.mapCancellation());
  }

  private Bundle getAuthorizationBundle(String merchantName, String sku, String serviceName) {
    if (sku != null && merchantName != null) {
      final Bundle bundle = new Bundle();
      bundle.putString(PaymentActivity.EXTRA_SKU, sku);
      bundle.putString(PaymentActivity.EXTRA_MERCHANT_NAME, merchantName);
      bundle.putString(PaymentActivity.EXTRA_SERVICE_NAME, serviceName);
      return bundle;
    }

    throw new IllegalArgumentException("Invalid product. Only in-app and paid apps supported");
  }

  private PayPalResult map(Result result) {
    switch (result.getResultCode()) {
      case Activity.RESULT_OK:
        final PaymentConfirmation confirmation = result.getData()
            .getParcelableExtra(
                com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
        if (confirmation != null && confirmation.getProofOfPayment() != null) {
          return new BillingNavigator.PayPalResult(BillingNavigator.PayPalResult.SUCCESS,
              confirmation.getProofOfPayment()
                  .getPaymentId());
        } else {
          return new BillingNavigator.PayPalResult(BillingNavigator.PayPalResult.ERROR, null);
        }
      case Activity.RESULT_CANCELED:
        return new BillingNavigator.PayPalResult(BillingNavigator.PayPalResult.CANCELLED, null);
      case PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID:
      default:
        return new BillingNavigator.PayPalResult(BillingNavigator.PayPalResult.ERROR, null);
    }
  }

  public static class PayPalResult {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int CANCELLED = 2;

    private final int status;
    private final String paymentConfirmationId;

    public PayPalResult(int status, String paymentConfirmationId) {
      this.status = status;
      this.paymentConfirmationId = paymentConfirmationId;
    }

    public int getStatus() {
      return status;
    }

    public String getPaymentConfirmationId() {
      return paymentConfirmationId;
    }
  }
}