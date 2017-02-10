/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * Created by rmateus on 07-03-2014.
 */
public class AdultDialog extends DialogFragment {

  /**
   * @param activity using activity instead of context allow us to show the snack bar when pin is
   * wrong
   */
  public static Dialog buildMaturePinInputDialog(final Activity activity,
      final DialogInterface.OnClickListener positiveButtonListener) {

    final View view =
        LayoutInflater.from(activity).inflate(R.layout.dialog_requestpin, null, false);
    final EditText pinEditText = ((EditText) view.findViewById(R.id.pininput));
    final String expectedPin = Integer.toString(SecurePreferences.getAdultContentPin(), 10);

    return new AlertDialog.Builder(activity).setView(view)
        .setMessage(R.string.request_adult_pin)
        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
          String insertedPin = pinEditText.getText().toString();
          if (TextUtils.equals(insertedPin, expectedPin)) {
            positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
            dialogInterface.dismiss();
          } else {
            ShowMessage.asSnack(activity, R.string.adult_pin_wrong);
            buildMaturePinInputDialog(activity, positiveButtonListener).show();
          }
        })
        .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          dialogInterface.dismiss();
          positiveButtonListener.onClick(null, Dialog.BUTTON_NEGATIVE);
        })
        .create();
  }

  public static Dialog setAdultPinDialog(final Context context, final Preference mp,
      final DialogInterface.OnClickListener positiveButtonListener) {
    final View view = LayoutInflater.from(context).inflate(R.layout.dialog_requestpin, null, false);
    final EditText pinEditText = ((EditText) view.findViewById(R.id.pininput));

    return new AlertDialog.Builder(context).setMessage(R.string.asksetadultpinmessage)
        .setView(view)
        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
          dialogInterface.dismiss();
          String insertedPin = pinEditText.getText().toString();
          if (!TextUtils.isEmpty(insertedPin)) {
            SecurePreferences.setAdultContentPin(Integer.valueOf(insertedPin));
            mp.setTitle(R.string.remove_mature_pin_title);
            mp.setSummary(R.string.remove_mature_pin_summary);
          }
          positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
        })
        .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          dialogInterface.dismiss();
          positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
        })
        .create();
  }

  private static Dialog dialogAsk21(final Context context,
      final DialogInterface.OnClickListener positiveButtonlistener,
      DialogInterface.OnCancelListener cancelListener) {

    AlertDialog.Builder builder =
        new AlertDialog.Builder(context).setMessage(R.string.are_you_adult)
            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
              Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
              Analytics.AdultContent.unlock();
              positiveButtonlistener.onClick(dialogInterface, DialogInterface.BUTTON_POSITIVE);
              dialogInterface.dismiss();
            })
            .setNegativeButton(R.string.no, (dialogInterface, i) -> {
              dialogInterface.dismiss();
              if (cancelListener != null) {
                cancelListener.onCancel(dialogInterface);
              }
            });
    if (cancelListener != null) {
      builder.setOnCancelListener(dialogInterface -> cancelListener.onCancel(dialogInterface));
    }
    return builder.create();
  }

  public static Dialog buildAreYouAdultDialog(final Activity c,
      final DialogInterface.OnClickListener positiveButtonlistener) {
    return buildAreYouAdultDialog(c, positiveButtonlistener, null);
  }

  public static Dialog buildAreYouAdultDialog(final Activity c,
      final DialogInterface.OnClickListener positiveButtonlistener,
      DialogInterface.OnCancelListener cancelListener) {
    int pin = SecurePreferences.getAdultContentPin();
    if (pin == -1) {
      return dialogAsk21(c, positiveButtonlistener, cancelListener);
    } else {
      return buildMaturePinInputDialog(c, positiveButtonlistener);
    }
  }

  @Override public void onDetach() {
    super.onDetach();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    return buildAreYouAdultDialog(getActivity(), new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
          SecurePreferences.setAdultSwitch(true);
        }
      }
    });
  }
}
