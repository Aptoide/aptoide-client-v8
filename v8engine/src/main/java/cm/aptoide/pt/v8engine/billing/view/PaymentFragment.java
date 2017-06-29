package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.presenter.PaymentMethodSelector;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentFragment extends FragmentView implements PaymentView {

  private View overlay;
  private View body;
  private View progressView;
  private RadioGroup paymentRadioGroup;
  private ImageView productIcon;
  private TextView productName;
  private TextView productDescription;
  private TextView noPaymentsText;
  private Button cancelButton;
  private Button buyButton;
  private TextView productPrice;

  private AlertDialog networkErrorDialog;
  private AlertDialog unknownErrorDialog;
  private SparseArray<PaymentMethodViewModel> paymentMap;
  private SpannableFactory spannableFactory;

  private boolean paymentLoading;
  private boolean transactionLoading;

  private ProductProvider productProvider;
  private Billing billing;
  private AptoideAccountManager accountManager;
  private PaymentAnalytics paymentAnalytics;
  private PaymentMethodSelector paymentMethodSelector;
  private AccountNavigator accountNavigator;
  private PaymentNavigator paymentNavigator;

  public static Fragment create(long appId, String storeName, boolean sponsored) {
    final Bundle bundle = ProductProvider.createBundle(appId, storeName, sponsored);
    final PaymentFragment fragment = new PaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static Fragment create(Bundle bundle) {
    final PaymentFragment fragment = new PaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static Fragment create(int apiVersion, String packageName, String sku, String type,
      String developerPayload) {
    final Bundle bundle =
        ProductProvider.createBundle(apiVersion, packageName, type, sku, developerPayload);
    final PaymentFragment fragment = new PaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    paymentAnalytics = ((V8Engine) getContext().getApplicationContext()).getPaymentAnalytics();
    paymentMethodSelector = new PaymentMethodSelector(BuildConfig.DEFAULT_PAYMENT_ID,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    paymentNavigator =
        new PaymentNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    spannableFactory = new SpannableFactory();
    overlay = view.findViewById(R.id.fragment_payment_overlay);
    progressView = view.findViewById(R.id.fragment_payment_global_progress_bar);
    noPaymentsText = (TextView) view.findViewById(R.id.fragment_payment_no_payments_text);

    productIcon = (ImageView) view.findViewById(R.id.fragment_payment_product_icon);
    productName = (TextView) view.findViewById(R.id.fragment_payment_product_name);
    productDescription = (TextView) view.findViewById(R.id.fragment_payment_product_description);

    body = view.findViewById(R.id.fragment_payment_body);
    productPrice = (TextView) view.findViewById(R.id.fragment_product_price);
    paymentRadioGroup = (RadioGroup) view.findViewById(R.id.fragment_payment_list);

    cancelButton = (Button) view.findViewById(R.id.fragment_payment_cancel_button);
    buyButton = (Button) view.findViewById(R.id.fragment_payment_buy_button);

    paymentMap = new SparseArray<>();
    final ContextThemeWrapper dialogTheme =
        new ContextThemeWrapper(getContext(), R.style.AptoideThemeDefault);

    networkErrorDialog = new AlertDialog.Builder(dialogTheme).setMessage(R.string.connection_error)
        .setPositiveButton(android.R.string.ok, null)
        .create();
    unknownErrorDialog =
        new AlertDialog.Builder(dialogTheme).setMessage(R.string.all_message_general_error)
            .setPositiveButton(android.R.string.ok, null)
            .create();

    attachPresenter(
        new PaymentPresenter(this, billing, accountManager, paymentMethodSelector, accountNavigator,
            paymentNavigator, paymentAnalytics, productProvider), savedInstanceState);
  }

  @Override public void onDestroyView() {
    spannableFactory = null;
    overlay = null;
    progressView = null;
    noPaymentsText = null;
    productIcon = null;
    productName = null;
    productDescription = null;
    body = null;
    productPrice = null;
    paymentRadioGroup = null;
    cancelButton = null;
    buyButton = null;
    paymentMap = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    unknownErrorDialog = null;
    unknownErrorDialog.dismiss();
    paymentLoading = false;
    transactionLoading = false;
    super.onDestroyView();
  }

  @Override public Observable<PaymentMethodViewModel> paymentSelection() {
    return RxRadioGroup.checkedChanges(paymentRadioGroup)
        .map(paymentId -> paymentMap.get(paymentId))
        .filter(paymentMethodViewModel -> paymentMethodViewModel != null);
  }

  @Override public Observable<Void> cancellationSelection() {
    return RxView.clicks(cancelButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> tapOutsideSelection() {
    return RxView.clicks(overlay)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> buySelection() {
    return RxView.clicks(buyButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public void selectPayment(PaymentMethodViewModel payment) {
    paymentRadioGroup.check(payment.getId());
  }

  @Override public void showPaymentLoading() {
    paymentLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showTransactionLoading() {
    transactionLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showPayments(List<PaymentMethodViewModel> payments) {
    paymentRadioGroup.removeAllViews();
    noPaymentsText.setVisibility(View.GONE);
    body.setVisibility(View.VISIBLE);
    buyButton.setVisibility(View.VISIBLE);
    paymentMap.clear();

    RadioButton radioButton;
    CharSequence radioText;
    for (PaymentMethodViewModel payment : payments) {
      radioButton = (RadioButton) getActivity().getLayoutInflater()
          .inflate(R.layout.payment_item, paymentRadioGroup, false);
      radioButton.setId(payment.getId());
      if (TextUtils.isEmpty(payment.getDescription())) {
        radioText = payment.getName();
      } else {
        radioText = spannableFactory.createTextAppearanceSpan(getContext(),
            R.style.TextAppearance_Aptoide_Caption,
            payment.getName() + "\n" + payment.getDescription(), payment.getDescription());
      }
      radioButton.setText(radioText);

      paymentMap.append(payment.getId(), payment);
      paymentRadioGroup.addView(radioButton);
    }
  }

  @Override public void showProduct(Product product) {
    ImageLoader.with(getContext())
        .load(product.getIcon(), productIcon);
    productName.setText(product.getTitle());
    productDescription.setText(product.getDescription());
    productPrice.setText(product.getPrice()
        .getCurrencySymbol() + " " + product.getPrice()
        .getAmount());
  }

  @Override public void hidePaymentLoading() {
    paymentLoading = false;
    if (!transactionLoading) {
      progressView.setVisibility(View.GONE);
    }
  }

  @Override public void hideTransactionLoading() {
    transactionLoading = false;
    if (!paymentLoading) {
      progressView.setVisibility(View.GONE);
    }
  }

  @Override public void showPaymentsNotFoundMessage() {
    body.setVisibility(View.GONE);
    noPaymentsText.setVisibility(View.VISIBLE);
    buyButton.setVisibility(View.GONE);
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

  @Override public void hideAllErrors() {
    networkErrorDialog.dismiss();
    unknownErrorDialog.dismiss();
  }
}