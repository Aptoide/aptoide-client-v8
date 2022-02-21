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

public class AcceptGDPRDialog {

  private AlertDialog dialog;
  private Button acceptGDPR;
  private Button closeAptoideButton;
  private PublishSubject<AcceptGDPRDialogClickType> uiEvents;

  public AcceptGDPRDialog(Context context) {
    uiEvents = PublishSubject.create();
    dialog = new AlertDialog.Builder(context).create();
    View dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_accept_gdpr, null);
    dialog.setView(dialogView);
    acceptGDPR = dialogView.findViewById(R.id.accept_continue);
    closeAptoideButton = dialogView.findViewById(R.id.close_aptoide);

    setPrivacyPolicyLinks(dialogView, context, uiEvents);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    acceptGDPR.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext(AcceptGDPRDialogClickType.ACCEPT);
        dialog.dismiss();
      }
    });

    closeAptoideButton.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext(AcceptGDPRDialogClickType.CLOSE);
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
    if (acceptGDPR != null) {
      acceptGDPR.setOnClickListener(null);
    }
    if (closeAptoideButton != null) {
      closeAptoideButton.setOnClickListener(null);
    }
    acceptGDPR = null;
    closeAptoideButton = null;
    uiEvents = null;
  }

  public Observable<AcceptGDPRDialogClickType> dialogClicked() {
    return uiEvents;
  }

  private void setPrivacyPolicyLinks(View dialogView, Context context,
      PublishSubject<AcceptGDPRDialogClickType> uiEvents) {
    ClickableSpan termsAndConditionsClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (uiEvents != null) {
          uiEvents.onNext(AcceptGDPRDialogClickType.TERMS_AND_CONDITIONS);
        }
      }
    };

    ClickableSpan privacyPolicyClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (uiEvents != null) {
          uiEvents.onNext(AcceptGDPRDialogClickType.PRIVACY);
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

  public enum AcceptGDPRDialogClickType {
    ACCEPT, CLOSE, PRIVACY, TERMS_AND_CONDITIONS
  }
}
