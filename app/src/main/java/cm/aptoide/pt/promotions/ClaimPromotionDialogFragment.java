package cm.aptoide.pt.promotions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import cm.aptoide.pt.R;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.BaseDialogView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import javax.inject.Inject;
import rx.Observable;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class ClaimPromotionDialogFragment extends BaseDialogView
    implements ClaimPromotionDialogView {

  protected static final int WALLET_PERMISSIONS_INTENT_REQUEST_CODE = 123;
  protected static final int WALLET_VERIFICATION_INTENT_REQUEST_CODE = 124;
  private static final String WALLET_PERMISSIONS_INTENT_URI_ACTION =
      "appcoins://wallet/permissions/1";
  private static final String WALLET_PERMISSIONS_INTENT_EXTRA_KEY = "PERMISSION_NAME_KEY";
  private static final String WALLET_PERMISSIONS_INTENT_EXTRA_VALUE = "WALLET_ADDRESS";
  private static final String WALLET_VERIFICATION_INTENT_URI_ACTION =
      "appcoins://wallet/validation/1";
  private static final String VIEW = "view";
  private static final String WALLET = "wallet";
  private static final String CLAIMED = "claimed";
  private static final String SUCCESS = "success";
  private static final String GENERIC_ERROR = "error";
  private static final String PACKAGE_NAME = "package_name";

  @Inject ClaimPromotionDialogPresenter presenter;
  @Inject ClaimPromotionsManager claimPromotionsManager;
  @Inject IdsRepository idsRepository;
  @Inject PromotionsAnalytics promotionsAnalytics;
  @Inject ClaimPromotionsNavigator navigator;
  private ClipboardManager clipboard;
  private ProgressBar loading;
  private EditText walletAddressEdit;
  private Button getWalletAddressButton;
  private Button walletNextButton;
  private Button walletCancelButton;
  private ImageView walletMessageIcon;
  private View walletErrorView;
  private TextView genericMessageTitle;
  private TextView genericMessageBody;
  private TextView genericMessageButton;
  private Button genericErrorOkButton;

  private View insertWalletView;
  private View genericMessageView;
  private View genericErrorView;
  private TextView genericErrorViewMessage;
  private View updateWalletView;
  private Button cancelUpdateWalletButton;
  private Button updateWalletButton;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getFragmentComponent(savedInstanceState).inject(this);
    clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loading = view.findViewById(R.id.loading);
    walletAddressEdit = view.findViewById(R.id.wallet_edit);
    getWalletAddressButton = view.findViewById(R.id.get_wallet_button);
    walletNextButton = view.findViewById(R.id.wallet_continue_button);
    walletCancelButton = view.findViewById(R.id.wallet_cancel_button);
    walletMessageIcon = view.findViewById(R.id.wallet_message_icon);
    walletErrorView = view.findViewById(R.id.wallet_error_view);
    genericMessageTitle = view.findViewById(R.id.generic_message_title);
    genericMessageBody = view.findViewById(R.id.generic_message_body);
    genericMessageButton = view.findViewById(R.id.generic_message_button);
    genericErrorOkButton = view.findViewById(R.id.error_ok_button);

    insertWalletView = view.findViewById(R.id.insert_address_view);
    genericMessageView = view.findViewById(R.id.generic_message_view);
    genericErrorView = view.findViewById(R.id.generic_error);
    genericErrorViewMessage = view.findViewById(R.id.generic_error_message);
    updateWalletView = view.findViewById(R.id.update_wallet_view);
    cancelUpdateWalletButton = view.findViewById(R.id.cancel_wallet_update_button);
    updateWalletButton = view.findViewById(R.id.update_wallet_button);

    attachPresenter(presenter);

    handleRestoreViewState(savedInstanceState);
  }

  public void onResume() {
    super.onResume();

    Window window = getDialog().getWindow();
    Point size = new Point();

    Display display = window.getWindowManager()
        .getDefaultDisplay();
    display.getSize(size);

    int width = size.x;

    window.setLayout((int) (width * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    walletAddressEdit = null;
    getWalletAddressButton = null;
    walletNextButton = null;
    walletCancelButton = null;
    walletMessageIcon = null;
    walletErrorView = null;
    genericMessageTitle = null;
    genericMessageBody = null;
    genericMessageButton = null;
    genericErrorOkButton = null;

    insertWalletView = null;
    genericMessageView = null;
    genericErrorView = null;
    updateWalletView = null;
    cancelUpdateWalletButton = null;
    updateWalletButton = null;

    presenter.dispose();
    presenter = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (insertWalletView.getVisibility() == View.VISIBLE) {
      outState.putString(VIEW, WALLET);
    } else if (genericMessageView.getVisibility() == View.VISIBLE && genericMessageTitle.getText()
        .equals(getResources().getString(R.string.holidayspromotion_title_completed))) {
      outState.putString(VIEW, SUCCESS);
    } else if (genericMessageView.getVisibility() == View.VISIBLE && genericMessageTitle.getText()
        .equals(getResources().getString(R.string.holidayspromotion_title_error_claimed))) {
      outState.putString(VIEW, CLAIMED);
    } else if (genericErrorView.getVisibility() == View.VISIBLE) {
      outState.putString(VIEW, GENERIC_ERROR);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    getDialog().getWindow()
        .requestFeature(Window.FEATURE_NO_TITLE);
    return inflater.inflate(R.layout.promotions_claim_dialog, container, false);
  }

  @Override public Observable<String> getWalletClick() {
    return RxView.clicks(getWalletAddressButton)
        .map(__ -> getArguments().getString(PACKAGE_NAME));
  }

  @Override public Observable<ClaimPromotionsClickWrapper> continueWalletClick() {
    return RxView.clicks(walletNextButton)
        .map(__ -> new ClaimPromotionsClickWrapper(walletAddressEdit.getText()
            .toString(), getArguments().getString(PACKAGE_NAME)));
  }

  @Override public void sendWalletIntent() {
    AptoideUtils.SystemU.openApp(APPCOINS_WALLET_PACKAGE_NAME, getContext().getPackageManager(),
        getContext());
  }

  @Override public void showGenericError() {
    showErrorView(getString(R.string.error_occured));
  }

  @Override public void showLoading() {
    loading.setVisibility(View.VISIBLE);
    insertWalletView.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
    updateWalletView.setVisibility(View.GONE);
  }

  @Override public void showInvalidWalletAddress() {
    loading.setVisibility(View.GONE);
    showWalletView();
    walletAddressEdit.setText("");
    walletMessageIcon.setVisibility(View.GONE);
    walletErrorView.setVisibility(View.VISIBLE);
  }

  @Override public void showPromotionAlreadyClaimed() {
    loading.setVisibility(View.GONE);
    showGenericMessageView(getResources().getString(R.string.holidayspromotion_title_error_claimed),
        getResources().getString(R.string.holidayspromotion_short_error_claimed));
  }

  @Override public void showClaimSuccess() {
    loading.setVisibility(View.GONE);
    showGenericMessageView(getResources().getString(R.string.holidayspromotion_title_completed),
        getResources().getString(R.string.holidayspromotion_message_completed));
  }

  @Override public Observable<TextViewAfterTextChangeEvent> editTextChanges() {
    return RxTextView.afterTextChangeEvents(walletAddressEdit);
  }

  @Override public void handleEmptyEditText(Editable address) {
    if (address.toString()
        .equals("")) {
      walletMessageIcon.setVisibility(View.GONE);
    } else {
      walletMessageIcon.setVisibility(View.VISIBLE);
    }
    if (validateAddress(address.toString())) {
      enableNextButton();
      disableWalletButton();
      setValidWalletMessage();
    } else {
      disableNextButton();
      enableWalletButton();
      setInvalidWalletMessage();
    }
    walletErrorView.setVisibility(View.GONE);
  }

  @Override public Observable<Void> dismissGenericErrorClick() {
    return RxView.clicks(genericErrorOkButton);
  }

  @Override public Observable<String> walletCancelClick() {
    return RxView.clicks(walletCancelButton)
        .map(__ -> getArguments().getString(PACKAGE_NAME));
  }

  @Override public Observable<ClaimDialogResultWrapper> dismissGenericMessage() {
    return RxView.clicks(genericMessageButton)
        .map(__ -> {
          if (genericMessageTitle.getText()
              .equals(getResources().getString(R.string.holidayspromotion_title_completed))) {
            return new ClaimDialogResultWrapper(getArguments().getString(PACKAGE_NAME), true);
          } else {
            return new ClaimDialogResultWrapper(getArguments().getString(PACKAGE_NAME), false);
          }
        });
  }

  @Override public void dismissDialog() {
    dismiss();
  }

  @Override public void fetchWalletAddressByIntent() {
    if (walletErrorView.getVisibility() != View.VISIBLE) {
      navigator.fetchWalletAddressByIntent(WALLET_PERMISSIONS_INTENT_URI_ACTION,
          WALLET_PERMISSIONS_INTENT_REQUEST_CODE, WALLET_PERMISSIONS_INTENT_EXTRA_KEY,
          WALLET_PERMISSIONS_INTENT_EXTRA_VALUE);
    }
  }

  @Override public Observable<Result> getActivityResults() {
    return ((ActivityResultNavigator) getContext()).results();
  }

  @Override public void updateWalletText(String walletAddress) {
    if (validateAddress(walletAddress)) {
      walletAddressEdit.setText(walletAddress);
    }
  }

  @Override public void fetchWalletAddressByClipboard() {
    if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription()
        .hasMimeType(MIMETYPE_TEXT_PLAIN)) {
      ClipData.Item item = clipboard.getPrimaryClip()
          .getItemAt(0);
      String address = item.getText()
          .toString();
      updateWalletText(address);
    }
  }

  @Override public void verifyWallet() {
    if (walletErrorView.getVisibility() != View.VISIBLE) {
      navigator.validateWallet(WALLET_VERIFICATION_INTENT_URI_ACTION,
          WALLET_VERIFICATION_INTENT_REQUEST_CODE);
    }
  }

  @Override public void showCanceledVerificationError() {
    showErrorView(getString(R.string.appc_verification_cancelled_by_user_message));
  }

  @Override public void showUpdateWalletDialog() {
    loading.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
    updateWalletView.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> onCancelWalletUpdate() {
    return RxView.clicks(cancelUpdateWalletButton);
  }

  @Override public Observable<Void> onUpdateWalletClick() {
    return RxView.clicks(updateWalletButton);
  }

  private void showErrorView(String errorMessage) {
    loading.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.VISIBLE);
    insertWalletView.setVisibility(View.GONE);
    updateWalletView.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
    genericErrorViewMessage.setText(errorMessage);
  }

  private boolean validateAddress(String address) {
    if (address != null) {
      return address.matches("(^(0x))([0-9a-f]{40})$");
    } else {
      return false;
    }
  }

  private void enableNextButton() {
    walletNextButton.setClickable(true);
    walletNextButton.setFocusable(true);
    walletNextButton.setTextColor(themeManager.getAttributeForTheme(R.attr.colorPrimaryDark).data);
  }

  private void disableNextButton() {
    walletNextButton.setClickable(false);
    walletNextButton.setFocusable(false);
    walletNextButton.setTextColor(getResources().getColor(R.color.grey_fog_light));
  }

  private void disableWalletButton() {
    getWalletAddressButton.setClickable(false);
    getWalletAddressButton.setFocusable(false);
    getWalletAddressButton.setBackgroundColor(
        themeManager.getAttributeForTheme(R.attr.lockedWalletButtonColor).data);
  }

  private void enableWalletButton() {
    getWalletAddressButton.setClickable(true);
    getWalletAddressButton.setFocusable(true);
    getWalletAddressButton.setBackgroundResource(R.drawable.aptoide_gradient);
  }

  private void setValidWalletMessage() {
    walletMessageIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green));
  }

  private void setInvalidWalletMessage() {
    walletMessageIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
  }

  private void showWalletView() {
    walletErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.VISIBLE);
    updateWalletView.setVisibility(View.GONE);
  }

  private void showGenericMessageView(String title, String body) {
    walletErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.GONE);
    updateWalletView.setVisibility(View.GONE);
    genericMessageTitle.setText(title);
    genericMessageBody.setText(body);
    genericMessageView.setVisibility(View.VISIBLE);
  }

  private void handleRestoreViewState(Bundle savedInstanceState) {
    if (savedInstanceState != null && savedInstanceState.getString(VIEW) != null) {
      String show = savedInstanceState.getString(VIEW);
      switch (show) {
        default:
        case WALLET:
          showWalletView();
          break;
        case CLAIMED:
          showPromotionAlreadyClaimed();
          break;
        case SUCCESS:
          showClaimSuccess();
          break;
        case GENERIC_ERROR:
          showGenericError();
          break;
      }
    }
  }
}
