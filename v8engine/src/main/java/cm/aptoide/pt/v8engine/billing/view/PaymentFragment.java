package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentFragment extends PermissionServiceFragment implements PaymentView {

  private View overlay;
  private View progressView;
  private RadioGroup paymentRadioGroup;
  private ImageView productIcon;
  private TextView productName;
  private TextView productDescription;
  private TextView noPaymentsText;
  private Button cancelButton;
  private Button buyButton;
  private TextView productPrice;

  private RxAlertDialog networkErrorDialog;
  private RxAlertDialog unknownErrorDialog;
  private SparseArray<PaymentMethodViewModel> paymentMap;
  private SpannableFactory spannableFactory;

  private boolean paymentLoading;
  private boolean transactionLoading;
  private boolean buyLoading;

  private ProductProvider productProvider;
  private Billing billing;
  private AptoideAccountManager accountManager;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;

  public static Fragment create(Bundle bundle) {
    final PaymentFragment fragment = new PaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    billingAnalytics = ((V8Engine) getContext().getApplicationContext()).getBillingAnalytics();
    billingNavigator =
        new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator(), getFragmentNavigator(), accountManager);
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

    productIcon = (ImageView) view.findViewById(R.id.include_payment_product_icon);
    productName = (TextView) view.findViewById(R.id.include_payment_product_name);
    productDescription = (TextView) view.findViewById(R.id.include_payment_product_description);

    productPrice = (TextView) view.findViewById(R.id.include_payment_product_price);
    paymentRadioGroup = (RadioGroup) view.findViewById(R.id.fragment_payment_list);

    cancelButton = (Button) view.findViewById(R.id.include_payment_buttons_cancel_button);
    buyButton = (Button) view.findViewById(R.id.include_payment_buttons_buy_button);

    paymentMap = new SparseArray<>();

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(android.R.string.ok)
            .build();
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(android.R.string.ok)
            .build();

    attachPresenter(
        new PaymentPresenter(this, billing, billingNavigator, billingAnalytics, productProvider),
        savedInstanceState);
  }

  @Override public void onDestroyView() {
    spannableFactory = null;
    overlay = null;
    progressView = null;
    noPaymentsText = null;
    productIcon = null;
    productName = null;
    productDescription = null;
    productPrice = null;
    paymentRadioGroup = null;
    cancelButton = null;
    buyButton = null;
    paymentMap = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    unknownErrorDialog.dismiss();
    unknownErrorDialog = null;
    paymentLoading = false;
    transactionLoading = false;
    buyLoading = false;
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

  @Override public void showBuyLoading() {
    buyLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showPayments(List<PaymentMethodViewModel> payments) {
    paymentRadioGroup.removeAllViews();
    noPaymentsText.setVisibility(View.GONE);
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
    if (!transactionLoading && !buyLoading) {
      progressView.setVisibility(View.GONE);
    }
  }

  @Override public void hideTransactionLoading() {
    transactionLoading = false;
    if (!paymentLoading && !buyLoading) {
      progressView.setVisibility(View.GONE);
    }
  }

  @Override public void hideBuyLoading() {
    buyLoading = false;
    if (!paymentLoading && !transactionLoading) {
      progressView.setVisibility(View.GONE);
    }
  }

  @Override public void showPaymentsNotFoundMessage() {
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
}