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
  private TextView walletMessage;
  private View walletMessageView;
  private ImageView captcha;
  private EditText captchaEdit;
  private Button captchaNextButton;
  private Button captchaCancelButton;

  private View insertWalletView;
  private View captchaView;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);
    clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    walletAddressEdit = null;
    getWalletAddressButton = null;
    walletNextButton = null;
    walletCancelButton = null;
    walletMessageIcon = null;
    walletMessage = null;
    walletMessageView = null;
    captcha = null;
    captchaEdit = null;
    captchaNextButton = null;
    captchaCancelButton = null;

    insertWalletView = null;
    captchaView = null;

    presenter.dispose();
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
    walletMessage = view.findViewById(R.id.wallet_message);
    walletMessageView = view.findViewById(R.id.wallet_message_view);
    captcha = view.findViewById(R.id.captcha_container);
    captchaEdit = view.findViewById(R.id.captcha_edit);
    captchaNextButton = view.findViewById(R.id.captcha_continue_button);
    captchaCancelButton = view.findViewById(R.id.captcha_cancel_button);

    insertWalletView = view.findViewById(R.id.insert_address_view);
    captchaView = view.findViewById(R.id.captcha_view);

    presenter = new ClaimPromotionDialogPresenter(this, new CompositeSubscription(),
        AndroidSchedulers.mainThread(), claimPromotionsManager, idsRepository);
    presenter.present();
    walletCancelButton.setOnClickListener(click -> dismiss());
    handleAddressEditRules();
    captchaCancelButton.setOnClickListener(click -> dismiss());
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

  @Override public Observable<String> continueClick() {
    return RxView.clicks(walletNextButton)
        .map(__ -> walletAddressEdit.getText()
            .toString());
  }

  @Override public void sendWalletIntent() {
    AptoideUtils.SystemU.openApp(WALLET_PACKAGE, getContext().getPackageManager(), getContext());
  }

  @Override public void showCaptchaView(String captchaUrl) {
    insertWalletView.setVisibility(View.GONE);
    captchaView.setVisibility(View.VISIBLE);
    ImageLoader.with(getContext())
        .loadWithRoundCorners(captchaUrl, 8, captcha, R.drawable.placeholder_square);
  }

  @Override public void showLoading() {
    loading.setVisibility(View.VISIBLE);
    insertWalletView.setVisibility(View.GONE);
    captchaView.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    loading.setVisibility(View.GONE);
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
          walletMessageView.setVisibility(View.GONE);
        } else {
          walletMessageView.setVisibility(View.VISIBLE);
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
      }
    });
  }

  private void handleClipboardPaste() {
    String address = null;

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
    walletMessage.setText("Wallet address valid");
    walletMessageIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green));
  }

  private void setInvalidWalletMessage() {
    walletMessage.setText("Wallet address invalid");
    walletMessageIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
  }
}
