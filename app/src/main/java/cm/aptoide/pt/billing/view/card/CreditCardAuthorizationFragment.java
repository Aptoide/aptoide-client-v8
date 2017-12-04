package cm.aptoide.pt.billing.view.card;

import adyen.com.adyencse.encrypter.ClientSideEncrypter;
import adyen.com.adyencse.encrypter.exception.EncrypterException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.adyen.core.models.Amount;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.PaymentDetails;
import com.adyen.core.utils.AmountUtil;
import com.adyen.core.utils.StringUtils;
import com.braintreepayments.cardform.view.CardForm;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CreditCardAuthorizationFragment extends PermissionServiceFragment
    implements CreditCardAuthorizationView {

  private static final String TAG = CreditCardAuthorizationFragment.class.getSimpleName();

  private View progressBar;
  private RxAlertDialog networkErrorDialog;
  private ClickHandler clickHandler;
  private View overlay;
  private CardForm cardForm;
  private Button buyButton;
  private Button cancelButton;
  private ImageView productIcon;
  private TextView productName;
  private TextView productDescription;
  private TextView productPrice;
  private TextView preAuthorizedCardText;

  private Billing billing;
  private BillingNavigator navigator;
  private BillingAnalytics analytics;
  private Adyen adyen;
  private PublishRelay<Void> backButton;
  private PublishRelay<Void> keyboardBuyRelay;
  private String publicKey;
  private String generationTime;
  private PaymentMethod paymentMethod;
  private boolean cvcOnly;
  private CheckBox rememberCardCheckBox;

  public static CreditCardAuthorizationFragment create(Bundle bundle) {
    final CreditCardAuthorizationFragment fragment = new CreditCardAuthorizationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling(
        getArguments().getString(BillingActivity.EXTRA_MERCHANT_NAME));
    navigator = ((ActivityResultNavigator) getActivity()).getBillingNavigator();
    analytics = ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    backButton = PublishRelay.create();
    keyboardBuyRelay = PublishRelay.create();
    adyen = ((AptoideApplication) getContext().getApplicationContext()).getAdyen();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    preAuthorizedCardText =
        (TextView) view.findViewById(R.id.fragment_credit_card_authorization_pre_authorized_card);
    progressBar = view.findViewById(R.id.fragment_credit_card_authorization_progress_bar);
    overlay = view.findViewById(R.id.fragment_credit_card_authorization_overlay);
    productIcon = (ImageView) view.findViewById(R.id.include_payment_product_icon);
    productName = (TextView) view.findViewById(R.id.include_payment_product_name);
    productDescription = (TextView) view.findViewById(R.id.include_payment_product_description);
    productPrice = (TextView) view.findViewById(R.id.include_payment_product_price);
    cancelButton = (Button) view.findViewById(R.id.include_payment_buttons_cancel_button);
    buyButton = (Button) view.findViewById(R.id.include_payment_buttons_buy_button);
    rememberCardCheckBox = (CheckBox) view.findViewById(
        R.id.fragment_credit_card_authorization_remember_card_check_box);
    buyButton.setVisibility(View.GONE);
    cardForm = (CardForm) view.findViewById(R.id.fragment_braintree_credit_card_form);

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

    cardForm.setOnCardFormValidListener(valid -> {
      if (valid) {
        buyButton.setVisibility(View.VISIBLE);
      } else {
        buyButton.setVisibility(View.GONE);
      }
    });
    cardForm.setOnCardFormSubmitListener(() -> {
      keyboardBuyRelay.call(null);
    });

    attachPresenter(new CreditCardAuthorizationPresenter(this,
        getArguments().getString(BillingActivity.EXTRA_SKU), billing, navigator, analytics,
        getArguments().getString(BillingActivity.EXTRA_SERVICE_NAME), adyen,
        AndroidSchedulers.mainThread()));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_credit_card_authorization, container, false);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(clickHandler);
    progressBar = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    overlay = null;
    productIcon = null;
    productName = null;
    productDescription = null;
    productPrice = null;
    cancelButton = null;
    rememberCardCheckBox = null;
    buyButton = null;
    preAuthorizedCardText = null;
    cardForm.setOnCardFormSubmitListener(null);
    cardForm.setOnCardFormValidListener(null);
    cardForm = null;
    super.onDestroyView();
  }

  @Override public void showProduct(Product product) {
    ImageLoader.with(getContext())
        .load(product.getIcon(), productIcon);
    productName.setText(product.getTitle());
    productDescription.setText(product.getDescription());
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

  @Override public Observable<PaymentDetails> creditCardDetailsEvent() {
    return Observable.merge(keyboardBuyRelay, RxView.clicks(buyButton))
        .map(__ -> getPaymentDetails(publicKey, generationTime));
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public Observable<Void> cancelEvent() {
    return Observable.merge(RxView.clicks(cancelButton), RxView.clicks(overlay), backButton);
  }

  @Override public void showCvcView(Amount amount, PaymentMethod paymentMethod) {
    cvcOnly = true;
    this.paymentMethod = paymentMethod;
    showProductPrice(amount);
    preAuthorizedCardText.setVisibility(View.VISIBLE);
    preAuthorizedCardText.setText(paymentMethod.getName());
    rememberCardCheckBox.setVisibility(View.GONE);
    cardForm.cardRequired(false)
        .expirationRequired(false)
        .cvvRequired(true)
        .postalCodeRequired(false)
        .mobileNumberRequired(false)
        .actionLabel(getString(R.string.buy))
        .setup(getActivity());
  }

  @Override
  public void showCreditCardView(PaymentMethod paymentMethod, Amount amount, boolean cvcRequired,
      boolean allowSave, String publicKey, String generationTime) {
    this.paymentMethod = paymentMethod;
    this.publicKey = publicKey;
    this.generationTime = generationTime;
    cvcOnly = false;
    preAuthorizedCardText.setVisibility(View.GONE);
    rememberCardCheckBox.setVisibility(View.VISIBLE);
    showProductPrice(amount);
    cardForm.cardRequired(true)
        .expirationRequired(true)
        .cvvRequired(cvcRequired)
        .postalCodeRequired(false)
        .mobileNumberRequired(false)
        .actionLabel(getString(R.string.buy))
        .setup(getActivity());
  }

  private void showProductPrice(Amount amount) {
    this.productPrice.setText(
        AmountUtil.format(amount, true, StringUtils.getLocale(getActivity())));
  }

  private PaymentDetails getPaymentDetails(String publicKey, String generationTime) {

    if (cvcOnly) {
      final PaymentDetails paymentDetails = new PaymentDetails(paymentMethod.getInputDetails());
      paymentDetails.fill("cardDetails.cvc", cardForm.getCvv());
      return paymentDetails;
    }

    final CreditCardPaymentDetails creditCardPaymentDetails =
        new CreditCardPaymentDetails(paymentMethod.getInputDetails());
    try {
      final JSONObject sensitiveData = new JSONObject();

      sensitiveData.put("holderName", "Checkout Shopper Placeholder");
      sensitiveData.put("number", cardForm.getCardNumber());
      sensitiveData.put("expiryMonth", cardForm.getExpirationMonth());
      sensitiveData.put("expiryYear", cardForm.getExpirationYear());
      sensitiveData.put("generationtime", generationTime);
      sensitiveData.put("cvc", cardForm.getCvv());
      creditCardPaymentDetails.fillCardToken(
          new ClientSideEncrypter(publicKey).encrypt(sensitiveData.toString()));
    } catch (JSONException e) {
      Log.e(TAG, "JSON Exception occurred while generating token.", e);
    } catch (EncrypterException e) {
      Log.e(TAG, "EncrypterException occurred while generating token.", e);
    }
    creditCardPaymentDetails.fillStoreDetails(rememberCardCheckBox.isChecked());
    return creditCardPaymentDetails;
  }
}
