package cm.aptoide.pt.promotions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.MainActivity;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClaimPromotionDialogFragment extends DialogFragment
    implements ClaimPromotionDialogView {

  private static final String WALLET_PACKAGE = "com.appcoins.wallet";
  private static final String VIEW = "view";
  private static final String WALLET = "wallet";
  private static final String CAPTCHA = "captcha";
  private static final String CLAIMED = "claimed";
  private static final String SUCCESS = "success";
  private static final String GENERIC_ERROR = "error";

  @Inject ClaimPromotionsManager claimPromotionsManager;
  @Inject IdsRepository idsRepository;
  private ClipboardManager clipboard;
  private ClaimPromotionDialogPresenter presenter;
  private ProgressBar loading;
  private EditText walletAddressEdit;
  private Button getWalletAddressButton;
  private Button walletNextButton;
  private Button walletCancelButton;
  private ImageView walletMessageIcon;
  private View walletErrorView;
  private ImageView captcha;
  private EditText captchaEdit;
  private ImageView refreshCaptchaButton;
  private ProgressBar captchaLoading;
  private Button captchaNextButton;
  private Button captchaCancelButton;
  private View captchaErrorView;
  private TextView genericMessageTitle;
  private TextView genericMessageBody;
  private TextView genericMessageButton;
  private Button genericErrorOkButton;

  private View insertWalletView;
  private View captchaView;
  private View genericMessageView;
  private View genericErrorView;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);
    clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (insertWalletView.getVisibility() == View.VISIBLE) {
      outState.putString(VIEW, WALLET);
    } else if (captchaView.getVisibility() == View.VISIBLE) {
      outState.putString(VIEW, CAPTCHA);
    } else if (genericMessageView.getVisibility() == View.VISIBLE && genericMessageTitle.getText()
        .equals(getResources().getString(R.string.holidayspromotion_title_completed))) {
      outState.putString(VIEW, SUCCESS);
    } else if (genericMessageView.getVisibility() == View.VISIBLE && genericMessageTitle.getText()
        .equals("Sorry")) {
      outState.putString(VIEW, CLAIMED);
    } else if (genericErrorView.getVisibility() == View.VISIBLE) {
      outState.putString(VIEW, GENERIC_ERROR);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    walletAddressEdit = null;
    getWalletAddressButton = null;
    walletNextButton = null;
    walletCancelButton = null;
    walletMessageIcon = null;
    walletErrorView = null;
    captcha = null;
    captchaEdit = null;
    captchaNextButton = null;
    captchaCancelButton = null;

    insertWalletView = null;
    captchaView = null;

    presenter.dispose();
    presenter = null;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    getDialog().getWindow()
        .requestFeature(Window.FEATURE_NO_TITLE);
    return inflater.inflate(R.layout.promotions_claim_dialog, container, false);
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
    captcha = view.findViewById(R.id.captcha_container);
    captchaEdit = view.findViewById(R.id.captcha_edit);
    refreshCaptchaButton = view.findViewById(R.id.captcha_refresh);
    captchaLoading = view.findViewById(R.id.captcha_progress);
    captchaNextButton = view.findViewById(R.id.captcha_continue_button);
    captchaCancelButton = view.findViewById(R.id.captcha_cancel_button);
    captchaErrorView = view.findViewById(R.id.captcha_error_view);
    genericMessageTitle = view.findViewById(R.id.generic_message_title);
    genericMessageBody = view.findViewById(R.id.generic_message_body);
    genericMessageButton = view.findViewById(R.id.generic_message_button);
    genericErrorOkButton = view.findViewById(R.id.error_ok_button);

    insertWalletView = view.findViewById(R.id.insert_address_view);
    captchaView = view.findViewById(R.id.captcha_view);
    genericMessageView = view.findViewById(R.id.generic_message_view);
    genericErrorView = view.findViewById(R.id.generic_error);

    presenter = new ClaimPromotionDialogPresenter(this, new CompositeSubscription(),
        AndroidSchedulers.mainThread(), claimPromotionsManager, idsRepository);
    presenter.present();
    walletCancelButton.setOnClickListener(click -> dismiss());
    handleAddressEditRules();
    captchaCancelButton.setOnClickListener(click -> dismiss());
    genericMessageButton.setOnClickListener(click -> dismiss());
    genericErrorOkButton.setOnClickListener(click -> dismiss());
    handleRestoreViewState(savedInstanceState);
  }

  public void onResume() {
    super.onResume();
    handleClipboardPaste();

    Window window = getDialog().getWindow();
    Point size = new Point();

    Display display = window.getWindowManager()
        .getDefaultDisplay();
    display.getSize(size);

    int width = size.x;

    window.setLayout((int) (width * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public Observable<Void> getWalletClick() {
    return RxView.clicks(getWalletAddressButton);
  }

  @Override public Observable<String> continueWalletClick() {
    return RxView.clicks(walletNextButton)
        .map(__ -> walletAddressEdit.getText()
            .toString());
  }

  @Override public Observable<ClaimPromotionsSubmitWrapper> finishClick() {
    return RxView.clicks(captchaNextButton)
        .map(__ -> new ClaimPromotionsSubmitWrapper("this.is.package", captchaEdit.getText()
            .toString()));
  }

  @Override public Observable<Void> refreshCaptchaClick() {
    return RxView.clicks(refreshCaptchaButton);
  }

  @Override public void showLoadingCaptcha() {
    refreshCaptchaButton.setVisibility(View.GONE);
    captchaLoading.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoadingCaptcha(String captchaUrl) {
    refreshCaptchaButton.setVisibility(View.VISIBLE);
    captchaLoading.setVisibility(View.GONE);
    ImageLoader.with(getContext())
        .loadWithRoundCornersWithoutCache(captchaUrl, 8, captcha, -1);
  }

  @Override public void sendWalletIntent() {
    AptoideUtils.SystemU.openApp(WALLET_PACKAGE, getContext().getPackageManager(), getContext());
  }

  @Override public void showCaptchaView(String captchaUrl) {
    claimPromotionsManager.saveCaptchaUrl(captchaUrl);
    captchaErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.GONE);
    captchaView.setVisibility(View.VISIBLE);
    ImageLoader.with(getContext())
        .loadWithRoundCornersWithoutCache(captchaUrl, 8, captcha, -1);
  }

  @Override public void showGenericError() {
    hideLoading();
    genericErrorView.setVisibility(View.VISIBLE);
    captchaView.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    captchaView.setVisibility(View.INVISIBLE);
    loading.setVisibility(View.VISIBLE);
    insertWalletView.setVisibility(View.GONE);
    genericMessageView.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    loading.setVisibility(View.GONE);
  }

  @Override public void showInvalidWalletAddress() {
    hideLoading();
    showWalletView();
    clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
    walletAddressEdit.setText("");
    walletMessageIcon.setVisibility(View.GONE);
    walletErrorView.setVisibility(View.VISIBLE);
  }

  @Override public void showPromotionAlreadyClaimed() {
    hideLoading();
    showGenericMessageView("Sorry",
        getResources().getString(R.string.holidayspromotion_short_error_claimed));
  }

  @Override public void showInvalidCaptcha(String captcha) {
    hideLoading();
    showCaptchaView(captcha);
    captchaErrorView.setVisibility(View.VISIBLE);
  }

  @Override public void showClaimSuccess() {
    hideLoading();
    showGenericMessageView(getResources().getString(R.string.holidayspromotion_title_completed),
        getResources().getString(R.string.holidayspromotion_message_completed));
  }

  private void handleAddressEditRules() {
    walletAddressEdit.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (s.toString()
            .equals("")) {
          walletMessageIcon.setVisibility(View.GONE);
        } else {
          walletMessageIcon.setVisibility(View.VISIBLE);
        }
        if (validateAddress(s.toString())) {
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
    });
  }

  private void handleClipboardPaste() {
    String address;

    if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription()
        .hasMimeType(MIMETYPE_TEXT_PLAIN)) {
      ClipData.Item item = clipboard.getPrimaryClip()
          .getItemAt(0);
      address = item.getText()
          .toString();
      if (validateAddress(address)) {
        walletAddressEdit.setText(address);
      }
    }
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
    walletNextButton.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
  }

  private void disableNextButton() {
    walletNextButton.setClickable(false);
    walletNextButton.setFocusable(false);
    walletNextButton.setTextColor(getResources().getColor(R.color.grey_fog_light));
  }

  private void disableWalletButton() {
    getWalletAddressButton.setClickable(false);
    getWalletAddressButton.setFocusable(false);
    getWalletAddressButton.setBackgroundColor(getResources().getColor(R.color.grey_fog_light));
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
    captchaView.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.VISIBLE);
  }

  private void showGenericMessageView(String title, String body) {
    walletErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    captchaView.setVisibility(View.GONE);
    insertWalletView.setVisibility(View.GONE);
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
        case CAPTCHA:
          showCaptchaView(claimPromotionsManager.getCaptchaUrl());
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
