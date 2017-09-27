package cm.aptoide.pt.billing.view;

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
import android.widget.ScrollView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.PaymentMethod;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.view.rx.RxAlertDialog;
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
  private SparseArray<PaymentMethod> paymentMap;
  private SpannableFactory spannableFactory;

  private boolean paymentLoading;
  private boolean transactionLoading;
  private boolean buyLoading;

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;
  private ScrollView scroll;

  public static Fragment create(Bundle bundle) {
    final PaymentFragment fragment = new PaymentFragment();
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
    spannableFactory = new SpannableFactory();
    overlay = view.findViewById(R.id.fragment_payment_overlay);
    scroll = (ScrollView) view.findViewById(R.id.fragment_payment_scroll);
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

    attachPresenter(new PaymentPresenter(this, billing, billingNavigator, billingAnalytics,
        getArguments().getString(PaymentActivity.EXTRA_APPLICATION_ID),
        getArguments().getString(PaymentActivity.EXTRA_PRODUCT_ID),
        getArguments().getString(PaymentActivity.EXTRA_DEVELOPER_PAYLOAD)), savedInstanceState);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment, container, false);
  }

  @Override public void onDestroyView() {
    scroll = null;
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

  @Override public Observable<PaymentMethod> selectPaymentEvent() {
    return RxRadioGroup.checkedChanges(paymentRadioGroup)
        .map(paymentId -> paymentMap.get(paymentId))
        .filter(PaymentMethod -> PaymentMethod != null);
  }

  @Override public Observable<Void> cancelEvent() {
    return Observable.merge(RxView.clicks(cancelButton), RxView.clicks(overlay))
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> buyEvent() {
    return RxView.clicks(buyButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public void selectPayment(PaymentMethod payment) {
    paymentRadioGroup.check(payment.getId());
  }

  @Override public void showPaymentLoading() {
    paymentLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showPurchaseLoading() {
    transactionLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showBuyLoading() {
    buyLoading = true;
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showPayments(List<PaymentMethod> payments) {
    paymentRadioGroup.removeAllViews();
    noPaymentsText.setVisibility(View.GONE);
    buyButton.setVisibility(View.VISIBLE);
    paymentMap.clear();

    RadioButton radioButton;
    CharSequence radioText;
    for (PaymentMethod payment : payments) {

      radioButton = (RadioButton) getActivity().getLayoutInflater()
          .inflate(R.layout.payment_item, paymentRadioGroup, false);
      radioButton.setId(payment.getId());

      if (payment.hasIcon()) {
        radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, payment.getIcon(), 0);
      }

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

  @Override public void hidePurchaseLoading() {

    if (transactionLoading) {
      scroll.postDelayed(new Runnable() {
        @Override public void run() {
          if (scroll != null) {
            scroll.fullScroll(View.FOCUS_DOWN);
          }
        }
      }, 500);
    }

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