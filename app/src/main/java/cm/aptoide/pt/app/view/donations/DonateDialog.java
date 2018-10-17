package cm.aptoide.pt.app.view.donations;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import cm.aptoide.pt.R;
import java.util.Locale;
import rx.subjects.PublishSubject;

public class DonateDialog extends AlertDialog {

  private static final int SEEKBAR_MAX = 5;
  private static final int SEEKBAR_START = 2;
  boolean shouldUpdate;
  private EditText nickname;
  private EditText appcValue;
  private SeekBar appcSlider;
  private Button donateButton;
  private Button cancelButton;
  private PublishSubject<DonationsDialogResult> donationButtonSubject;

  public DonateDialog(Context context,
      PublishSubject<DonationsDialogResult> donationButtonSubject) {
    super(context);
    this.donationButtonSubject = donationButtonSubject;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.appview_donations_dialog);
    this.nickname = findViewById(R.id.nickname);
    this.appcValue = findViewById(R.id.appc_value);
    this.appcSlider = findViewById(R.id.appc_slider);
    this.donateButton = findViewById(R.id.donate_button);
    this.cancelButton = findViewById(R.id.cancel_button);

    donateButton.setOnClickListener(view -> donationButtonSubject.onNext(new DonationsDialogResult(
        nickname.getText()
            .toString(), Float.parseFloat(appcValue.getText()
        .toString()
        .split(" ")[0]))));
    cancelButton.setOnClickListener(view -> dismiss());
    appcSlider.setMax(SEEKBAR_MAX);
    appcSlider.setProgress(SEEKBAR_START);
    appcValue.setText(String.format(Locale.getDefault(), "%d APPC", SEEKBAR_START));

    appcValue.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        int intLength = appcValue.getText()
            .toString()
            .split(" ")[0].length();
        if (start > intLength) {
          appcValue.setSelection(intLength);
        }
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void afterTextChanged(Editable editable) {
        if (!editable.toString()
            .split(" ")[0].equals("")) {
          float value = Float.parseFloat(editable.toString()
              .split(" ")[0]);
          if (value >= 0 && value <= appcSlider.getMax()) {
            shouldUpdate = true;
            appcSlider.setProgress(Math.round(value));
          } else {
            shouldUpdate = false;
            appcSlider.setProgress(Math.round(value));
          }
        }
      }
    });
    appcSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (shouldUpdate) {
          appcValue.setText(String.format(Locale.getDefault(), "%d APPC", i));
          appcValue.setSelection(appcValue.getText()
              .toString()
              .split(" ")[0].length());
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        shouldUpdate = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        shouldUpdate = false;
      }
    });
  }
}
