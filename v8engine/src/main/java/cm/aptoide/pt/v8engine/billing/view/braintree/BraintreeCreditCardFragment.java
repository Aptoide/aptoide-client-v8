package cm.aptoide.pt.v8engine.billing.view.braintree;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.billing.view.ProductProvider;
import cm.aptoide.pt.v8engine.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BraintreeCreditCardFragment extends PermissionServiceFragment
    implements BraintreeCreditCardView {

  private static final String EXTRA_PAYMENT_METHOD_ID =
      "cm.aptoide.pt.v8engine.billing.view.extra.PAYMENT_METHOD_ID";
  private Braintree braintree;
  private PublishRelay<CardBuilder> cardBuilderRelay;
  private View progressBar;
  private View overlay;
  private CardForm cardForm;
  private CardType cardType;
  private RxAlertDialog unknownErrorDialog;
  private ProductProvider productProvider;
  private Billing billing;
  private Button buyButton;
  private Button cancelButton;
  private ImageView productIcon;
  private TextView productName;
  private TextView productDescription;
  private TextView productPrice;
  private int paymentId;
  private AptoideAccountManager accountManager;

  public static Fragment create(Bundle bundle, int paymentMethodId) {
    final BraintreeCreditCardFragment fragment = new BraintreeCreditCardFragment();
    bundle.putInt(EXTRA_PAYMENT_METHOD_ID, paymentMethodId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof Braintree) {
      braintree = (Braintree) activity;
    } else {
      throw new IllegalStateException("Activity must implement " + Braintree.class.getSimpleName());
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cardBuilderRelay = PublishRelay.create();
    billing = ((V8Engine) getContext().getApplicationContext()).getBilling();
    productProvider = ProductProvider.fromBundle(billing, getArguments());
    paymentId = getArguments().getInt(EXTRA_PAYMENT_METHOD_ID);
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_braintree_credit_card, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    overlay = view.findViewById(R.id.fragment_payment_overlay);
    productIcon = (ImageView) view.findViewById(R.id.include_payment_product_icon);
    productName = (TextView) view.findViewById(R.id.include_payment_product_name);
    productDescription = (TextView) view.findViewById(R.id.include_payment_product_description);
    productPrice = (TextView) view.findViewById(R.id.include_payment_product_price);

    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();

    cancelButton = (Button) view.findViewById(R.id.include_payment_buttons_cancel_button);
    buyButton = (Button) view.findViewById(R.id.include_payment_buttons_buy_button);
    buyButton.setVisibility(View.GONE);
    progressBar = view.findViewById(R.id.fragment_braintree_progress_bar);
    cardForm = (CardForm) view.findViewById(R.id.fragment_braintree_credit_card_form);
    cardForm.setOnFormFieldFocusedListener(field -> {
      if (!(field instanceof CardEditText) && !TextUtils.isEmpty(cardForm.getCardNumber())) {
        final CardType computedCardType = CardType.forCardNumber(cardForm.getCardNumber());
        if (cardType != computedCardType) {
          cardType = computedCardType;
        }
      }
    });
    cardForm.setOnCardFormValidListener(valid -> {
      if (valid) {
        buyButton.setVisibility(View.VISIBLE);
      } else {
        buyButton.setVisibility(View.GONE);
      }
    });
    cardForm.setOnCardFormSubmitListener(() -> {
      cardBuilderRelay.call(createCard());
    });
    attachPresenter(new BraintreePresenter(this, braintree, productProvider, billing,
        new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator(), getFragmentNavigator(), accountManager),
        AndroidSchedulers.mainThread(), paymentId), savedInstanceState);
  }

  @Override public void onDestroyView() {
    overlay = null;
    productIcon = null;
    productName = null;
    productDescription = null;
    productPrice = null;
    cancelButton = null;
    buyButton = null;
    progressBar = null;
    cardForm.setOnFormFieldFocusedListener(null);
    cardForm.setOnCardFormSubmitListener(null);
    cardForm.setOnCardFormValidListener(null);
    cardForm = null;
    super.onDestroyView();
  }

  @Override public void showCreditCardForm(Configuration configuration) {
    cardForm.cardRequired(true)
        .expirationRequired(true)
        .cvvRequired(configuration.isCvvChallengePresent())
        .postalCodeRequired(configuration.isPostalCodeChallengePresent())
        .mobileNumberRequired(false)
        .actionLabel(getString(R.string.buy))
        .setup(getActivity());
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showError() {
    unknownErrorDialog.show();
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

  @Override public Observable<CardBuilder> creditCardEvent() {
    return Observable.merge(cardBuilderRelay, RxView.clicks(buyButton)
        .map(__ -> createCard()));
  }

  @Override public Observable<Void> errorDismissedEvent() {
    return unknownErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }

  @Override public Observable<Void> cancellationEvent() {
    return RxView.clicks(cancelButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> tapOutsideSelection() {
    return RxView.clicks(overlay)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  private CardBuilder createCard() {
    return new CardBuilder().cardNumber(cardForm.getCardNumber())
        .expirationMonth(cardForm.getExpirationMonth())
        .expirationYear(cardForm.getExpirationYear())
        .cvv(cardForm.getCvv())
        .postalCode(cardForm.getPostalCode());
  }
}
