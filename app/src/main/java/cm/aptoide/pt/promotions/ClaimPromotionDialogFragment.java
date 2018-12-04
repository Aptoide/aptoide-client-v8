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
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClaimPromotionDialogFragment extends DialogFragment
    implements ClaimPromotionDialogView {

  private static final String WALLET_PACKAGE = "com.appcoins.wallet";

  private ClipboardManager clipboard;
  private ClaimPromotionDialogPresenter presenter;
  private EditText walletAddressEdit;
  private Button getWalletAddressButton;
  private Button nextButton;
  private Button cancelButton;
  private ImageView walletMessageIcon;
  private TextView walletMessage;
  private View walletMessageView;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    walletAddressEdit = null;
    getWalletAddressButton = null;
    nextButton = null;
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
    walletAddressEdit = view.findViewById(R.id.wallet_edit);
    getWalletAddressButton = view.findViewById(R.id.get_wallet_button);
    nextButton = view.findViewById(R.id.continue_button);
    cancelButton = view.findViewById(R.id.cancel_button);
    walletMessageIcon = view.findViewById(R.id.wallet_message_icon);
    walletMessage = view.findViewById(R.id.wallet_message);
    walletMessageView = view.findViewById(R.id.wallet_message_view);
    presenter = new ClaimPromotionDialogPresenter(this, new CompositeSubscription(),
        AndroidSchedulers.mainThread(), null);
    presenter.present();
    cancelButton.setOnClickListener(click -> dismiss());
    handleAddressEditRules();
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
    return RxView.clicks(nextButton)
        .map(__ -> walletAddressEdit.getText()
            .toString());
  }

  @Override public void sendWalletIntent() {
    AptoideUtils.SystemU.openApp(WALLET_PACKAGE, getContext().getPackageManager(), getContext());
  }

  @Override public void showCaptcha() {
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
    nextButton.setClickable(true);
    nextButton.setFocusable(true);
    nextButton.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
  }

  private void disableNextButton() {
    nextButton.setClickable(false);
    nextButton.setFocusable(false);
    nextButton.setTextColor(getResources().getColor(R.color.grey_fog_light));
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
