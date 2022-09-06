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
import cm.aptoide.analytics.implementation.CrashLogger;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LoggedInTermsAndConditionsDialog {

  private static final String GDPR_DIALOG_EVENT_LISTENER_IS_NULL =
      "GDPR_DIALOG_EVENT_LISTENER_IS_NULL";
  private AlertDialog dialog;
  private Button continueButton;
  private Button logoutButton;
  private PublishSubject<String> uiEvents;
  private CrashLogger crashReport;

  public LoggedInTermsAndConditionsDialog(Context context) {
    uiEvents = PublishSubject.create();
    crashReport = CrashReport.getInstance();
    LayoutInflater inflater = LayoutInflater.from(context);
    dialog = new AlertDialog.Builder(context).create();

    View dialogView = inflater.inflate(R.layout.dialog_logged_in_accept_tos, null);
    dialog.setView(dialogView);
    continueButton = dialogView.findViewById(R.id.accept_continue);
    logoutButton = dialogView.findViewById(R.id.log_out);

    setPrivacyPolicyLinks(dialogView, context, uiEvents);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    continueButton.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext("continue");
        dialog.dismiss();
      } else {
        crashReport.log(GDPR_DIALOG_EVENT_LISTENER_IS_NULL, "");
      }
    });

    logoutButton.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext("logout");
      } else {
        crashReport.log(GDPR_DIALOG_EVENT_LISTENER_IS_NULL, "");
      }
      dialog.dismiss();
    });
  }

  public void showDialog() {
    dialog.show();
  }

  public void destroyDialog() {
    if (dialog.isShowing()) {
      dialog.dismiss();
    }
    dialog = null;
    if (continueButton != null) {
      continueButton.setOnClickListener(null);
    }
    if (logoutButton != null) {
      logoutButton.setOnClickListener(null);
    }
    continueButton = null;
    logoutButton = null;
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
        } else {
          crashReport.log(GDPR_DIALOG_EVENT_LISTENER_IS_NULL, "");
        }
      }
    };

    ClickableSpan privacyPolicyClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (uiEvents != null) {
          uiEvents.onNext("privacy");
        } else {
          crashReport.log(GDPR_DIALOG_EVENT_LISTENER_IS_NULL, "");
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
