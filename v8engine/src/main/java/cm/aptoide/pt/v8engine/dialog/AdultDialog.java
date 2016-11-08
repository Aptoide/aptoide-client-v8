/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import cm.aptoide.pt.dialog.AndroidBasicDialog;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * Created by rmateus on 07-03-2014.
 */
public class AdultDialog extends DialogFragment {

  public static Dialog buildMaturePinInputDialog(final Context context,
      final DialogInterface.OnClickListener positiveButtonListener) {

    final View view = LayoutInflater.from(context).inflate(R.layout.dialog_requestpin, null, false);
    final EditText pinEditText = ((EditText) view.findViewById(R.id.pininput));
    final String expectedPin = Integer.toString(SecurePreferences.getAdultContentPin(), 10);

    AndroidBasicDialog dialog = AndroidBasicDialog.build(context, view);
    dialog.setMessage(R.string.request_adult_pin);
    dialog.setPositiveButton(android.R.string.ok, v -> {
      dialog.dismiss();
      String insertedPin = pinEditText.getText().toString();
      if (TextUtils.equals(insertedPin, expectedPin)) {
        //FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Pin");
        positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
      } else {
        //FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Wrong_Pin");
        ShowMessage.asSnack(view, R.string.adult_pin_wrong);
        buildMaturePinInputDialog(context, positiveButtonListener).show();
      }
    });

    dialog.setNegativeButton(android.R.string.cancel, v -> {
      dialog.dismiss();
      positiveButtonListener.onClick(null, Dialog.BUTTON_NEGATIVE);
    });

    return dialog.getCreatedDialog();
  }

  public static Dialog setAdultPinDialog(final Context context, final Preference mp,
      final DialogInterface.OnClickListener positiveButtonListener) {

    //final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_requestpin, null);
    //AlertDialog.Builder builder =
    //    new AlertDialog.Builder(getActivity()).setMessage(R.string.asksetadultpinmessage)
    //        .setView(view)
    //        .setPositiveButton(R.string.setpin, (dialog, which) -> {
    //          String input = ((EditText) view.findViewById(R.id.pininput)).getText().toString();
    //          if (!TextUtils.isEmpty(input)) {
    //            SecurePreferences.setAdultContentPin(Integer.valueOf(input));
    //            mp.setTitle(R.string.remove_mature_pin_title);
    //            mp.setSummary(R.string.remove_mature_pin_summary);
    //          }
    //          isSetingPIN = false;
    //        })
    //        .setNegativeButton(android.R.string.cancel, (dialog, which) -> isSetingPIN = false);
    //
    //AlertDialog alertDialog = builder.create();
    //
    //alertDialog.setOnDismissListener(dialog -> isSetingPIN = false);
    //
    //return alertDialog;

    final View view = LayoutInflater.from(context).inflate(R.layout.dialog_requestpin, null, false);
    final EditText pinEditText = ((EditText) view.findViewById(R.id.pininput));

    AndroidBasicDialog dialog = AndroidBasicDialog.build(context, view);
    dialog.setMessage(R.string.asksetadultpinmessage);
    dialog.setPositiveButton(android.R.string.ok, v -> {
      dialog.dismiss();
      String insertedPin = pinEditText.getText().toString();
      if (!TextUtils.isEmpty(insertedPin)) {
        SecurePreferences.setAdultContentPin(Integer.valueOf(insertedPin));
        mp.setTitle(R.string.remove_mature_pin_title);
        mp.setSummary(R.string.remove_mature_pin_summary);
      }
      positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
    });

    dialog.setNegativeButton(android.R.string.cancel, v -> {
      dialog.dismiss();
      positiveButtonListener.onClick(null, Dialog.BUTTON_POSITIVE);
    });

    return dialog.getCreatedDialog();
  }

  private static Dialog dialogAsk21(final Context context,
      final DialogInterface.OnClickListener positiveButtonListener) {

		/*
    return new android.app.AlertDialog.Builder(c).setMessage(c.getString(R.string.are_you_adult))
				.setPositiveButton(R.string.yes, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
						Analytics.AdultContent.unlock();
						positiveButtonlistener.onClick(dialog, which);
					}
				})
				.setNegativeButton(R.string.no, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.create();
		*/

    AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
    dialog.setMessage(R.string.are_you_adult);
    dialog.setPositiveButton(R.string.yes, v -> {
      Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
      Analytics.AdultContent.unlock();
      positiveButtonListener.onClick(dialog.getCreatedDialog(), DialogInterface.BUTTON_POSITIVE);
      dialog.dismiss();
    });

    dialog.setNegativeButton(R.string.no, v -> {
      dialog.dismiss();
    });

    return dialog.getCreatedDialog();

    // FIXME: 16/08/16 sithengineer use the next line instead
    //return GenericDialogs.createGenericYesNoCancelMessage(c, "", c.getString(R.string.are_you_adult));
  }

  private static Dialog dialogAsk21(final Context context,
      final DialogInterface.OnClickListener positiveButtonlistener,
      DialogInterface.OnCancelListener cancelListener) {

    /*
    return new android.app.AlertDialog.Builder(c).setMessage(c.getString(R.string.are_you_adult))
				.setPositiveButton(R.string.yes, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						positiveButtonlistener.onClick(dialog, which);
						Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
						Analytics.AdultContent.unlock();
					}
				})
				.setNegativeButton(R.string.no, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						dialog.cancel();
						cancelListener.onCancel(dialog);
					}
				}).setOnCancelListener(dialog -> cancelListener.onCancel(dialog))
				.create();
				*/

    AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
    dialog.setMessage(R.string.are_you_adult);
    dialog.setPositiveButton(R.string.yes, v -> {
      Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
      Analytics.AdultContent.unlock();
      positiveButtonlistener.onClick(dialog.getCreatedDialog(), DialogInterface.BUTTON_POSITIVE);
      dialog.dismiss();
    });
    dialog.setNegativeButton(R.string.no, v -> {
      dialog.dismiss();
      cancelListener.onCancel(dialog.getCreatedDialog());
    }).setOnCancelListener(() -> {
      cancelListener.onCancel(dialog.getCreatedDialog());
    });

    return dialog.getCreatedDialog();
  }

  public static Dialog buildAreYouAdultDialog(final Context c,
      final DialogInterface.OnClickListener positiveButtonlistener) {
    int pin = SecurePreferences.getAdultContentPin();
    if (pin == -1) {
      return dialogAsk21(c, positiveButtonlistener);
    } else {
      return buildMaturePinInputDialog(c, positiveButtonlistener);
    }
  }

  public static Dialog buildAreYouAdultDialog(final Context c,
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
