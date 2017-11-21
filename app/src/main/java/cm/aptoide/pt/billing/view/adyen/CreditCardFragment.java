package cm.aptoide.pt.billing.view.adyen;

import adyen.com.adyencse.encrypter.ClientSideEncrypter;
import adyen.com.adyencse.encrypter.exception.EncrypterException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.R;
import com.adyen.cardscan.PaymentCard;
import com.adyen.cardscan.PaymentCardScanner;
import com.adyen.cardscan.PaymentCardScannerFactory;
import com.adyen.core.constants.Constants;
import com.adyen.core.models.Amount;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.InputDetail;
import com.adyen.core.models.paymentdetails.PaymentDetails;
import com.adyen.core.utils.AmountUtil;
import com.adyen.core.utils.StringUtils;
import com.adyen.ui.activities.CheckoutActivity;
import com.adyen.ui.adapters.InstallmentOptionsAdapter;
import com.adyen.ui.fragments.CreditCardFragmentBuilder;
import com.adyen.ui.utils.AdyenInputValidator;
import com.adyen.ui.views.CVCEditText;
import com.adyen.ui.views.CardHolderEditText;
import com.adyen.ui.views.CheckoutCheckBox;
import com.adyen.ui.views.CreditCardEditText;
import com.adyen.ui.views.ExpiryDateEditText;
import com.adyen.ui.views.loadinganimation.ThreeDotsLoadingView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.adyen.core.constants.Constants.DataKeys.AMOUNT;
import static com.adyen.core.constants.Constants.DataKeys.PAYMENT_METHOD;
import static com.adyen.core.models.paymentdetails.CreditCardPaymentDetails.INSTALLMENTS;

/**
 * Fragment for collecting {@link PaymentDetails} for credit card payments.
 * Should be instantiated via {@link CreditCardFragmentBuilder}.
 */
public class CreditCardFragment extends Fragment
    implements CreditCardEditText.CVCFieldStatusListener, PaymentCardScanner.Listener {

  private static final String TAG = CreditCardFragment.class.getSimpleName();
  private CreditCardInfoListener creditCardInfoListener;
  private boolean nameRequired;
  private Amount amount;
  private String shopperReference;
  private PaymentMethod paymentMethod;
  private String publicKey;
  private String generationTime;

  private CreditCardEditText creditCardNoView;
  private ImageButton scanCardButton;
  private ExpiryDateEditText expiryDateView;
  private CVCEditText cvcView;
  private CardHolderEditText cardHolderEditText;
  private CheckoutCheckBox saveCardCheckBox;
  private Spinner installmentsSpinner;

  private LinearLayout cvcLayout;

  private CreditCardFragmentBuilder.CvcFieldStatus cvcFieldStatus =
      CreditCardFragmentBuilder.CvcFieldStatus.REQUIRED;

  private int theme;

  private List<PaymentCardScanner> paymentCardScanners;

  public static CreditCardFragment create(Amount amount, PaymentMethod paymentMethod,
      String publicKey, String shopperReference, String generationtime,
      CreditCardFragmentBuilder.CvcFieldStatus cvcFieldStatus, boolean paymentCardScanEnabled,
      int theme) {

    if (amount == null) {
      throw new IllegalStateException("Amount not set.");
    }
    if (paymentMethod == null) {
      throw new IllegalStateException("PaymentMethod not set.");
    }
    if (publicKey == null) {
      throw new IllegalStateException("PublicKey not set.");
    }
    if (generationtime == null) {
      throw new IllegalStateException("Generationtime not set.");
    }

    final CreditCardFragment fragment = new CreditCardFragment();
    final Bundle bundle = new Bundle();
    bundle.putSerializable(AMOUNT, amount);
    bundle.putString(Constants.DataKeys.SHOPPER_REFERENCE, shopperReference);
    bundle.putString(Constants.DataKeys.PUBLIC_KEY, publicKey);
    bundle.putString(Constants.DataKeys.GENERATION_TIME, generationtime);
    bundle.putSerializable(PAYMENT_METHOD, paymentMethod);
    bundle.putString(Constants.DataKeys.CVC_FIELD_STATUS, cvcFieldStatus.name());
    bundle.putBoolean(Constants.DataKeys.PAYMENT_CARD_SCAN_ENABLED, paymentCardScanEnabled);
    bundle.putInt("theme", theme);
    fragment.setArguments(bundle);
    return fragment;
  }

  public void setCreditCardInfoListener(
      @NonNull final CreditCardInfoListener creditCardInfoListener) {
    this.creditCardInfoListener = creditCardInfoListener;
  }

  @Override public void onScanStarted(@NonNull PaymentCardScanner paymentCardScanner) {
    // Do nothing.
  }

  @Override public void onScanCompleted(@NonNull PaymentCardScanner paymentCardScanner,
      @NonNull PaymentCard paymentCard) {
    creditCardNoView.setText(paymentCard.getCardNumber());

    Integer expiryMonth = paymentCard.getExpiryMonth();
    Integer expiryYear = paymentCard.getExpiryYear();

    if (expiryMonth != null && expiryYear != null) {
      expiryDateView.setText(expiryMonth + "/" + expiryYear);
    }

    cvcView.setText(paymentCard.getSecurityCode());
  }

  @Override public void onScanError(@NonNull PaymentCardScanner paymentCardScanner,
      @Nullable Throwable error) {
    String errorMessage = error != null ? error.getLocalizedMessage() : null;

    if (!TextUtils.isEmpty(errorMessage)) {
      Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    for (PaymentCardScanner paymentCardScanner : paymentCardScanners) {
      paymentCardScanner.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    amount = (Amount) getArguments().get(CheckoutActivity.AMOUNT);
    paymentMethod = (PaymentMethod) getArguments().get(CheckoutActivity.PAYMENT_METHOD);
    shopperReference = getArguments().getString(Constants.DataKeys.SHOPPER_REFERENCE);
    publicKey = getArguments().getString(Constants.DataKeys.PUBLIC_KEY);
    generationTime = getArguments().getString(Constants.DataKeys.GENERATION_TIME);
    cvcFieldStatus = CreditCardFragmentBuilder.CvcFieldStatus.valueOf(
        getArguments().getString(Constants.DataKeys.CVC_FIELD_STATUS));

    for (InputDetail inputDetail : paymentMethod.getInputDetails()) {
      if (inputDetail.getKey()
          .equals("cardHolderName")) {
        nameRequired = true;
      }
    }

    theme = getArguments().getInt("theme");

    paymentCardScanners = new ArrayList<>();

    if (getArguments().getBoolean(Constants.DataKeys.PAYMENT_CARD_SCAN_ENABLED)) {
      PaymentCardScannerFactory factory =
          PaymentCardScannerFactory.Loader.getPaymentCardScannerFactory(getContext());

      if (factory != null) {
        paymentCardScanners.addAll(factory.getPaymentCardScanners(getActivity()));
      }

      for (PaymentCardScanner paymentCardScanner : paymentCardScanners) {
        paymentCardScanner.setListener(this);
      }
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View fragmentView;

    final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), theme);
    LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
    fragmentView = localInflater.inflate(R.layout.credit_card_fragment, container, false);

    creditCardNoView = ((CreditCardEditText) fragmentView.findViewById(R.id.adyen_credit_card_no));
    scanCardButton = (ImageButton) fragmentView.findViewById(R.id.scan_card_button);
    scanCardButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        scanCardButton.showContextMenu();
      }
    });
    if (paymentCardScanners.size() == 1) {
      scanCardButton.setImageDrawable(paymentCardScanners.get(0)
          .getDisplayIcon());
    }
    registerForContextMenu(scanCardButton);
    scanCardButton.setVisibility(paymentCardScanners.isEmpty() ? View.GONE : View.VISIBLE);
    expiryDateView =
        ((ExpiryDateEditText) fragmentView.findViewById(R.id.adyen_credit_card_exp_date));
    cvcView = ((CVCEditText) fragmentView.findViewById(R.id.adyen_credit_card_cvc));
    cvcLayout = ((LinearLayout) fragmentView.findViewById(R.id.adyen_cvc_layout));

    cardHolderEditText =
        ((CardHolderEditText) fragmentView.findViewById(R.id.credit_card_holder_name));
    if (nameRequired) {
      final LinearLayout cardHolderLayout =
          ((LinearLayout) fragmentView.findViewById(R.id.card_holder_name_layout));
      cardHolderLayout.setVisibility(VISIBLE);
    }

    final Collection<InputDetail> inputDetails = paymentMethod.getInputDetails();
    for (final InputDetail inputDetail : inputDetails) {
      if (INSTALLMENTS.equals(inputDetail.getKey())) {
        fragmentView.findViewById(R.id.card_installments_area)
            .setVisibility(VISIBLE);
        final List<InputDetail.Item> installmentOptions = inputDetail.getItems();
        installmentsSpinner = (Spinner) fragmentView.findViewById(R.id.installments_spinner);
        final InstallmentOptionsAdapter installmentOptionsAdapter =
            new InstallmentOptionsAdapter(getActivity(), installmentOptions);
        installmentsSpinner.setAdapter(installmentOptionsAdapter);
        break;
      }
    }

    final Button collectDataButton = (Button) fragmentView.findViewById(R.id.collectCreditCardData);

    final TextView checkoutTextView = (TextView) fragmentView.findViewById(R.id.amount_text_view);
    final String amountString =
        AmountUtil.format(amount, true, StringUtils.getLocale(getActivity()));
    final String checkoutString = getString(R.string.pay_with_amount, amountString);
    checkoutTextView.setText(checkoutString);
    AdyenInputValidator validator = new AdyenInputValidator();
    validator.setOnReadyStateChangedListener(new AdyenInputValidator.OnReadyStateChangedListener() {
      @Override public void onReadyStateChanged(boolean isReady) {
        collectDataButton.setEnabled(isReady);
      }
    });
    creditCardNoView.setValidator(validator);
    creditCardNoView.setCVCEditText(cvcView);
    creditCardNoView.setLogoUrl(paymentMethod.getLogoUrl());
    creditCardNoView.initializeLogo();

    final Map<String, CreditCardFragmentBuilder.CvcFieldStatus> allowedCardTypes =
        getAllowedCardTypes();

    creditCardNoView.setAllowedCardTypes(allowedCardTypes);

    expiryDateView.setValidator(validator);

    if (cvcFieldStatus == CreditCardFragmentBuilder.CvcFieldStatus.NOCVC) {
      cvcView.setOptional(true);
      cvcLayout.setVisibility(GONE);
    } else {
      cvcLayout.setVisibility(VISIBLE);
      creditCardNoView.addCVCFieldStatusListener(this);
      cvcView.setValidator(validator);
    }

    if (nameRequired) {
      cardHolderEditText.setValidator(validator);
    }

    saveCardCheckBox = (CheckoutCheckBox) fragmentView.findViewById(R.id.save_card_checkbox);
    if (!StringUtils.isEmptyOrNull(shopperReference)) {
      fragmentView.findViewById(R.id.layout_save_card)
          .setVisibility(VISIBLE);
      fragmentView.findViewById(R.id.layout_click_area_save_card)
          .setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              saveCardCheckBox.forceRippleAnimation();
              saveCardCheckBox.toggle();
            }
          });
    } else {
      fragmentView.findViewById(R.id.layout_save_card)
          .setVisibility(GONE);
    }

    collectDataButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
        String token = getToken();

        boolean storeDetails =
            !StringUtils.isEmptyOrNull(shopperReference) && saveCardCheckBox.isChecked();

        if (creditCardInfoListener != null) {
          final CreditCardPaymentDetails creditCardPaymentDetails =
              new CreditCardPaymentDetails(inputDetails);
          creditCardPaymentDetails.fillCardToken(token);
          if (installmentsSpinner != null) {
            creditCardPaymentDetails.fillNumberOfInstallments(
                Short.valueOf(((InputDetail.Item) installmentsSpinner.getSelectedItem()).getId()));
          }
          creditCardPaymentDetails.fillStoreDetails(storeDetails);
          creditCardInfoListener.onCreditCardInfoProvided(creditCardPaymentDetails);
        } else {
          Log.w(TAG, "No listener provided.");
        }

        checkoutTextView.setVisibility(GONE);
        final ThreeDotsLoadingView progressBar =
            ((ThreeDotsLoadingView) fragmentView.findViewById(R.id.processing_progress_bar));
        progressBar.setVisibility(VISIBLE);

        cvcView.setEnabled(false);
        creditCardNoView.setEnabled(false);
        if (nameRequired) {
          cardHolderEditText.setEnabled(false);
        }
        expiryDateView.setEnabled(false);

        InputMethodManager inputManager =
            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
      }
    });

    if (getActivity() instanceof CheckoutActivity) {
      ((CheckoutActivity) getActivity()).setActionBarTitle(R.string.title_card_details);
    }
    return fragmentView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    CreditCardEditText creditCardNoView =
        ((CreditCardEditText) view.findViewById(R.id.adyen_credit_card_no));
    creditCardNoView.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(creditCardNoView, InputMethodManager.SHOW_IMPLICIT);
  }

  @Override public void onResume() {
    super.onResume();

    for (PaymentCardScanner paymentCardScanner : paymentCardScanners) {
      paymentCardScanner.onResume();
    }
  }

  @Override public void onPause() {
    super.onPause();

    for (PaymentCardScanner paymentCardScanner : paymentCardScanners) {
      paymentCardScanner.onPause();
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    if (v == scanCardButton) {
      int size = paymentCardScanners.size();

      if (size == 1) {
        PaymentCardScanner paymentCardScanner = paymentCardScanners.get(0);
        paymentCardScanner.startScan();
      } else if (size > 1) {
        for (int itemId = 0; itemId < size; itemId++) {
          PaymentCardScanner paymentCardScanner = paymentCardScanners.get(itemId);
          menu.add(Menu.NONE, itemId, Menu.NONE, paymentCardScanner.getDisplayDescription());
        }
      }
    }
  }

  @Override public boolean onContextItemSelected(MenuItem item) {
    int index = item.getItemId();

    if (index >= 0 && index < paymentCardScanners.size()) {
      PaymentCardScanner paymentCardScanner = paymentCardScanners.get(index);
      paymentCardScanner.startScan();
      return true;
    } else {
      return super.onContextItemSelected(item);
    }
  }

  @NonNull private Map<String, CreditCardFragmentBuilder.CvcFieldStatus> getAllowedCardTypes() {
    final Map<String, CreditCardFragmentBuilder.CvcFieldStatus> allowedCardTypes = new HashMap<>();
    final List<PaymentMethod> memberPaymentMethods = paymentMethod.getMemberPaymentMethods();
    if (memberPaymentMethods != null) {
      for (final PaymentMethod memberPaymentMethod : memberPaymentMethods) {
        if (memberPaymentMethod.getInputDetails() != null && "true".equals(
            memberPaymentMethod.getConfiguration()
                .getNoCVC())) {
          allowedCardTypes.put(memberPaymentMethod.getType(),
              CreditCardFragmentBuilder.CvcFieldStatus.NOCVC);
        } else if (memberPaymentMethod.getConfiguration() != null && "true".equals(
            memberPaymentMethod.getConfiguration()
                .getCvcOptional())) {
          allowedCardTypes.put(memberPaymentMethod.getType(),
              CreditCardFragmentBuilder.CvcFieldStatus.OPTIONAL);
        } else {
          allowedCardTypes.put(memberPaymentMethod.getType(),
              CreditCardFragmentBuilder.CvcFieldStatus.REQUIRED);
        }
      }
    } else {
      if ("true".equals(paymentMethod.getConfiguration()
          .getNoCVC())) {
        allowedCardTypes.put(paymentMethod.getType(),
            CreditCardFragmentBuilder.CvcFieldStatus.NOCVC);
      } else if ("true".equals(paymentMethod.getConfiguration()
          .getCvcOptional())) {
        allowedCardTypes.put(paymentMethod.getType(),
            CreditCardFragmentBuilder.CvcFieldStatus.OPTIONAL);
      } else {
        allowedCardTypes.put(paymentMethod.getType(),
            CreditCardFragmentBuilder.CvcFieldStatus.REQUIRED);
      }
    }
    return allowedCardTypes;
  }

  private String getToken() {
    if (!inputFieldsAvailable()) {
      return null;
    }
    if (TextUtils.isEmpty(publicKey)) {
      Log.e(TAG, "Public key is not available; credit card payment cannot be handled.");
      return "";
    }
    final JSONObject sensitiveData = new JSONObject();
    try {

      if (nameRequired) {
        sensitiveData.put("holderName", cardHolderEditText.getText());
      } else {
        sensitiveData.put("holderName", "Checkout Shopper Placeholder");
      }
      sensitiveData.put("number", creditCardNoView.getCCNumber());

      sensitiveData.put("expiryMonth", expiryDateView.getMonth());
      sensitiveData.put("expiryYear", expiryDateView.getFullYear());
      sensitiveData.put("generationtime", generationTime);
      sensitiveData.put("cvc", cvcView.getCVC());
      ClientSideEncrypter encrypter = new ClientSideEncrypter(publicKey);
      return encrypter.encrypt(sensitiveData.toString());
    } catch (JSONException e) {
      Log.e(TAG, "JSON Exception occurred while generating token.", e);
    } catch (EncrypterException e) {
      Log.e(TAG, "EncrypterException occurred while generating token.", e);
    }
    return "";
  }

  private boolean inputFieldsAvailable() {
    if (creditCardNoView == null
        || expiryDateView == null
        || cvcView == null
        || cardHolderEditText == null
        || saveCardCheckBox == null) {
      return false;
    }
    return true;
  }

  @Override public void onCVCFieldStatusChanged(
      final CreditCardFragmentBuilder.CvcFieldStatus cvcFieldStatus) {
    this.cvcFieldStatus = cvcFieldStatus;

    if (cvcFieldStatus == CreditCardFragmentBuilder.CvcFieldStatus.NOCVC) {
      cvcLayout.setVisibility(GONE);
    } else {
      cvcLayout.setVisibility(VISIBLE);
      if (cvcFieldStatus == CreditCardFragmentBuilder.CvcFieldStatus.OPTIONAL) {
        cvcView.setOptional(true);
      } else {
        cvcView.setOptional(false);
      }
    }
  }

  /**
   * The listener interface for receiving the card payment details.
   */
  public interface CreditCardInfoListener {
    void onCreditCardInfoProvided(CreditCardPaymentDetails creditCardPaymentDetails);
  }
}
