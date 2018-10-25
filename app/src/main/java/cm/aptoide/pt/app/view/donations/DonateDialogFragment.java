package cm.aptoide.pt.app.view.donations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.donations.utils.GenericPaymentIntentBuilder;
import cm.aptoide.pt.view.MainActivity;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class DonateDialogFragment extends DialogFragment implements DonateDialogView {
  private static final int RC_REQUEST = 666;
  private static final int SEEKBAR_MAX = 20; //20 steps
  private static final int SEEKBAR_START = 2;
  private static final int MAX = 125000; //50

  private static final String PACKAGE_NAME = "package_name";
  private static final String HAS_WALLET = "wallet";

  @Inject DonationsService donationsService;
  @Inject AppNavigator appNavigator;
  boolean textUpdate;
  boolean sliderUpdate;
  private String packageName;
  private EditText nickname;
  private EditText appcValue;
  private SeekBar appcSlider;
  private Button donateButton;
  private Button cancelButton;
  private View donationsView;
  private ProgressBar donationsProgress;
  private View noWalletView;
  private Button noWalletCancelButton;
  private Button noWalletContinueButton;
  private View errorView;
  private Button errorOkButton;

  private DonateDialogPresenter presenter;
  private InputMethodManager imm;

  public static DonateDialogFragment newInstance(String packageName, boolean hasWallet) {
    Bundle args = new Bundle();
    DonateDialogFragment fragment = new DonateDialogFragment();
    args.putString(PACKAGE_NAME, packageName);
    args.putBoolean(HAS_WALLET, hasWallet);
    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);
    packageName = getArguments().getString(PACKAGE_NAME);
    presenter = new DonateDialogPresenter(this, donationsService, new CompositeSubscription(),
        AndroidSchedulers.mainThread(), appNavigator);
    textUpdate = true;
    sliderUpdate = true;
    imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    nickname = null;
    appcValue = null;
    appcSlider = null;
    donateButton = null;
    cancelButton = null;
    donationsView = null;
    donationsProgress = null;
  }

  private void setValueInsertProperties() {
    appcValue.setText(String.valueOf(SEEKBAR_START));
    appcValue.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        int intLength = appcValue.getText()
            .toString()
            .length();
        appcValue.setSelection(intLength);
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void afterTextChanged(Editable editable) {
        float value = 0;
        String strValue = editable.toString();

        if (!strValue.equals("")) {
          if (strValue.substring(0, 1)
              .equals(".")) {
            strValue = "0".concat(strValue);
          }
          value = Float.parseFloat(strValue);
        }

        if (value >= 0 && value <= MAX) {
          // shouldUpdate = value == Math.round(value);
          //appcSlider.setProgress((int) Math.round(Math.log((value-A)/B)/C));
          //appcSlider.setProgress((int)Math.floor(logX(E, (value/C))));
          textUpdate = false;
          if (sliderUpdate) {
            appcSlider.setProgress((int) (Math.sqrt(((value) / (MAX)) * 1000.0f * 1000.0f)));
          }
          textUpdate = true;
        } else {
          //shouldUpdate = false;
          appcSlider.setProgress(Math.round(value));
        }
      }
    });
  }

  private void setSliderProperties() {
    appcSlider.setMax(SEEKBAR_MAX);
    appcSlider.setProgress(SEEKBAR_START);
    appcSlider.incrementProgressBy(1);
    appcSlider.getProgressDrawable()
        .setColorFilter(new PorterDuffColorFilter(getContext().getResources()
            .getColor(R.color.default_orange_gradient_end), PorterDuff.Mode.SRC_IN));
    appcSlider.getThumb()
        .setColorFilter(getContext().getResources()
            .getColor(R.color.default_orange_gradient_end), PorterDuff.Mode.SRC_IN);
    appcSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        sliderUpdate = false;
        if (textUpdate) {
          appcValue.setText(String.valueOf(Math.round((i * i) / (1000.0f * 1000.0f) * (MAX))));
        }
        sliderUpdate = true;
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        if (imm != null) {
          imm.hideSoftInputFromWindow(getView().getRootView()
              .getWindowToken(), 0);
        }
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  @Override public Observable<DonationsDialogResult> donateClick() {
    return RxView.clicks(donateButton)
        .map(click -> new DonationsDialogResult(packageName, nickname.getText()
            .toString(), Float.parseFloat(appcValue.getText()
            .toString())));
  }

  @Override public Observable<Void> noWalletContinueClick() {
    return RxView.clicks(noWalletContinueButton);
  }

  @Override
  public void sendWalletIntent(float value, String address, String packageName, String nickname) {
    PendingIntent intent =
        GenericPaymentIntentBuilder.buildBuyIntent(getContext(), "donation", String.valueOf(value),
            address, packageName, GenericPaymentIntentBuilder.TransactionData.TYPE_DONATION,
            nickname, true);
    try {
      startIntentSenderForResult(intent.getIntentSender(), RC_REQUEST, new Intent(), 0, 0, 0, null);
    } catch (IntentSender.SendIntentException e) {
      e.printStackTrace();
    }
    dismiss();
  }

  @Override public void showLoading() {
    donationsView.setVisibility(View.GONE);
    donationsProgress.setVisibility(View.VISIBLE);
  }

  @Override public void showNoWalletView() {
    donationsProgress.setVisibility(View.GONE);
    noWalletView.setVisibility(View.VISIBLE);
  }

  @Override public void dismissDialog() {
    dismiss();
  }

  @Override public void showErrorMessage() {

  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.appview_donations_dialog, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.nickname = view.findViewById(R.id.nickname);
    nickname.setImeOptions(EditorInfo.IME_ACTION_DONE);
    nickname.setSingleLine();
    this.appcValue = view.findViewById(R.id.appc_value);
    this.appcSlider = view.findViewById(R.id.appc_slider);
    this.donateButton = view.findViewById(R.id.donate_button);
    this.cancelButton = view.findViewById(R.id.cancel_button);
    this.donationsView = view.findViewById(R.id.donations_view);
    this.donationsProgress = view.findViewById(R.id.donations_progress);
    this.noWalletView = view.findViewById(R.id.no_wallet_layout);
    this.noWalletCancelButton = view.findViewById(R.id.no_wallet_cancel_button);
    this.noWalletContinueButton = view.findViewById(R.id.no_wallet_continue_button);
    this.errorView = view.findViewById(R.id.error_layout);
    this.errorOkButton = view.findViewById(R.id.error_ok_button);
    chooseViewToPresent(getArguments().getBoolean(HAS_WALLET, true));
    presenter.present();
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

  @Override public void onDestroy() {
    super.onDestroy();
    packageName = null;
    presenter.dispose();
    presenter = null;
  }

  private void chooseViewToPresent(boolean hasWallet) {
    donationsView.setOnClickListener(click -> {
      if (imm != null) {
        imm.hideSoftInputFromWindow(getView().getRootView()
            .getWindowToken(), 0);
      }
    });
    if (hasWallet) {
      setSliderProperties();
      setValueInsertProperties();
      cancelButton.setOnClickListener(click -> dismiss());
    } else {
      donationsView.setVisibility(View.GONE);
      showNoWalletView();
      noWalletCancelButton.setOnClickListener(click -> dismiss());
    }
  }
}