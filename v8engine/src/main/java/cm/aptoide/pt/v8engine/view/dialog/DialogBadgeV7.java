/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.v8engine.R;

import static cm.aptoide.pt.model.v7.Malware.Reason.Status;

/**
 * Created by hsousa on 18/11/15.
 */
public class DialogBadgeV7 extends BaseDialog {

  protected Malware malware;
  protected String appName;
  protected Malware.Rank rank;

  public static DialogBadgeV7 newInstance(Malware malware, String appName, Malware.Rank rank) {

    DialogBadgeV7 dialog = new DialogBadgeV7();
    dialog.malware = malware;
    dialog.appName = appName;
    dialog.rank = rank;
    return dialog;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
    } else {
      setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
    }
    setRetainInstance(true);
  }

  @Override public void onStart() {
    super.onStart();
    getDialog().getWindow()
        .setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(android.R.color.transparent)));
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    @SuppressLint("InflateParams") final View v =
        LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_badge, null);
    AlertDialog builder = new AlertDialog.Builder(getActivity()).setView(v).create();

    v.findViewById(R.id.dialog_ok_button).setOnClickListener(v1 -> dismiss());

    if (malware != null && malware.getRank() != null) {

      switch (malware.getRank()) {
        case TRUSTED:
          v.findViewById(R.id.trusted_header_layout).setVisibility(View.VISIBLE);
          break;
        case WARNING:
          v.findViewById(R.id.warning_header_layout).setVisibility(View.VISIBLE);
          break;
        case UNKNOWN:
          v.findViewById(R.id.unknown_header_layout).setVisibility(View.VISIBLE);
          v.findViewById(R.id.tr_unknown).setVisibility(View.VISIBLE);
          // Doesn't need to do more logic, exit.
          return builder;
      }
    }

    if (malware != null && malware.getReason() != null) {
      if (malware.getReason().getScanned() != null
          && malware.getReason().getScanned().getStatus() != null
          && (Status.passed.equals(malware.getReason().getScanned().getStatus())
          || Status.warn.equals(malware.getReason().getScanned().getStatus()))) {

        if (malware.getReason().getScanned().getAvInfo() != null) {
          v.findViewById(R.id.tr_scanned).setVisibility(View.VISIBLE);
        }
      }

      if (malware.getReason().getThirdpartyValidated() != null
          && Malware.GOOGLE_PLAY.equalsIgnoreCase(
          malware.getReason().getThirdpartyValidated().getStore())) {
        v.findViewById(R.id.tr_third_party).setVisibility(View.VISIBLE);
      }

      if (malware.getReason().getSignatureValidated() != null
          && malware.getReason().getSignatureValidated().getStatus() != null) {

        switch (malware.getReason().getSignatureValidated().getStatus()) {
          case passed:
            v.findViewById(R.id.tr_signature).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tv_reason_signature_validation)).setText(
                getString(R.string.reason_signature));
            break;
          case failed:
            // still in study by the UX team
            v.findViewById(R.id.tr_signature).setVisibility(View.VISIBLE);
            v.findViewById(R.id.iv_signature).setVisibility(View.INVISIBLE);
            ((TextView) v.findViewById(R.id.tv_reason_signature_validation)).setText(
                getString(R.string.reason_failed));
            break;
          case blacklisted:
            // still in study by the UX team
            //                        v.findViewById(R.id.malware.getReason()_signature_not_validated).setVisibility(View.VISIBLE);
            //                        ((TextView) v.findViewById(R.id.malware.getReason()_signature_not_validated)).setText
            // (getString(R
            // .string.application_signature_blacklisted));
            break;
        }
      }

      if (malware.getReason().getManual() != null
          && malware.getReason().getManual().getStatus() != null
          && Status.passed.equals(malware.getReason().getManual().getStatus())) {
        v.findViewById(R.id.tr_manual).setVisibility(View.VISIBLE);
      }
    }

    return builder;
  }
}
