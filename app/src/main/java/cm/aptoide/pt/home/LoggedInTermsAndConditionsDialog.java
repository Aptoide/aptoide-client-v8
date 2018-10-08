package cm.aptoide.pt.home;

import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 20/09/2018.
 */

public class LoggedInTermsAndConditionsDialog {

  private AlertDialog dialog;
  private PublishSubject<String> uiEvents;

  public LoggedInTermsAndConditionsDialog(Context context) {
    uiEvents = PublishSubject.create();
    LayoutInflater inflater = LayoutInflater.from(context);
    dialog = new AlertDialog.Builder(context).create();
    View dialogView = inflater.inflate(R.layout.dialog_logged_in_accept_tos, null);
    dialog.setView(dialogView);
    Button continueButton = dialogView.findViewById(R.id.accept_continue);

    setPrivacyPolicyLinks(dialogView, context, uiEvents);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    continueButton.setOnClickListener(__ -> {
      uiEvents.onNext("continue");
      dialog.dismiss();
    });

    dialogView.findViewById(R.id.log_out)
        .setOnClickListener(__ -> {
          uiEvents.onNext("logout");
          dialog.dismiss();
        });
  }

  public void showDialog() {
    dialog.show();
  }

  public void destroyDialog() {
    dialog = null;
    uiEvents = null;
  }

  public Observable<String> dialogClicked() {
    return uiEvents;
  }

  private void setPrivacyPolicyLinks(View dialogView, Context context,
      PublishSubject<String> uiEvents) {
    ClickableSpan termsAndConditionsClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (uiEvents != null) {
          uiEvents.onNext("terms");
        }
      }
    };

    ClickableSpan privacyPolicyClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (uiEvents != null) {
          uiEvents.onNext("privacy");
        }
      }
    };

    String baseString = context.getString(R.string.accept_terms_message_loggedin);
    String buttonString = context.getString(R.string.terms_and_conditions_privacy_sign_up_message);
    String termsAndConditionsPlaceHolder = context.getString(R.string.settings_terms_conditions);
    String privacyPolicyPlaceHolder = context.getString(R.string.settings_privacy_policy);
    String privacyAndTerms =
        String.format(baseString, termsAndConditionsPlaceHolder, privacyPolicyPlaceHolder);
    String buttonAccept =
        String.format(buttonString, termsAndConditionsPlaceHolder, privacyPolicyPlaceHolder);
    Button continueButton = dialogView.findViewById(R.id.accept_continue);
    continueButton.setText(buttonAccept);

    SpannableString privacyAndTermsSpan = new SpannableString(privacyAndTerms);
    privacyAndTermsSpan.setSpan(termsAndConditionsClickListener,
        privacyAndTerms.indexOf(termsAndConditionsPlaceHolder),
        privacyAndTerms.indexOf(termsAndConditionsPlaceHolder)
            + termsAndConditionsPlaceHolder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    privacyAndTermsSpan.setSpan(privacyPolicyClickListener,
        privacyAndTerms.indexOf(privacyPolicyPlaceHolder),
        privacyAndTerms.indexOf(privacyPolicyPlaceHolder) + privacyPolicyPlaceHolder.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    TextView info = dialogView.findViewById(R.id.tos_info);
    info.setText(privacyAndTermsSpan);
    info.setMovementMethod(LinkMovementMethod.getInstance());
  }
}
