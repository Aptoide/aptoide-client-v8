package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompra;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolPoints;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPal;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
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

  public BillingNavigator(PurchaseBundleMapper bundleMapper, ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator) {
    this.bundleMapper = bundleMapper;
    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToTransactionAuthorizationView(PaymentMethod paymentMethod, Product product) {

    if (paymentMethod instanceof PayPal) {
      fragmentNavigator.navigateTo(PayPalFragment.create(getProductBundle(product)));
      return;
    }

    if (paymentMethod instanceof MolPoints) {
      fragmentNavigator.navigateTo(MolFragment.create(getProductBundle(product)));
      return;
    }

    if (paymentMethod instanceof BoaCompra) {
      fragmentNavigator.navigateTo(
          BoaCompraFragment.create(getProductBundle(product), paymentMethod.getId()));
    }

    throw new IllegalArgumentException("Invalid payment method id "
        + paymentMethod.getId()
        + " can not navigate to authorization view");
  }

  public void popAuthorizedPaymentView() {
    fragmentNavigator.popBackStack();
  }

  public void popLocalPaymentView() {
    fragmentNavigator.popBackStack();
  }

  public void navigateToPayPalForResult(int requestCode, String currency, String description,
      double amount) {

    final Bundle bundle = new Bundle();
    bundle.putParcelable(PayPalService.EXTRA_PAYPAL_CONFIGURATION,
        new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
            .clientId(BuildConfig.PAYPAL_KEY)
            .merchantName(V8Engine.getConfiguration()
                .getMarketName()));
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

  public void popPaymentViewWithResult(Purchase purchase) {
    activityNavigator.finish(Activity.RESULT_OK, bundleMapper.map(purchase));
  }

  public void popPaymentViewWithResult(Throwable throwable) {
    activityNavigator.finish(Activity.RESULT_CANCELED, bundleMapper.map(throwable));
  }

  public void popPaymentViewWithResult() {
    activityNavigator.finish(Activity.RESULT_CANCELED, bundleMapper.mapCancellation());
  }

  private Bundle getProductBundle(Product product) {
    if (product instanceof InAppProduct) {
      return ProductProvider.createBundle(((InAppProduct) product).getApiVersion(),
          ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getType(),
          ((InAppProduct) product).getSku(), ((InAppProduct) product).getSku());
    } else if (product instanceof PaidAppProduct) {
      return ProductProvider.createBundle(((PaidAppProduct) product).getAppId(),
          ((PaidAppProduct) product).getStoreName(), ((PaidAppProduct) product).isSponsored());
    } else {
      throw new IllegalArgumentException("Invalid product. Only in-app and paid apps supported");
    }
  }

  private PayPalResult map(ActivityNavigator.Result result) {
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