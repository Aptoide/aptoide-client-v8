/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import cm.aptoide.pt.dialog.AndroidBasicDialog;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * Created by rmateus on 07-03-2014.
 */
public class AdultDialog extends DialogFragment {

	public static android.app.Dialog dialogRequestMaturepin(final android.content.Context context, final android.content.DialogInterface.OnClickListener positiveButtonlistener) {
		final android.view.View v = android.view.LayoutInflater.from(context).inflate(R.layout.dialog_requestpin,
                null);
		android.content.DialogInterface.OnClickListener onClickListener = new android.content.DialogInterface
                .OnClickListener() {
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {

				switch (which) {
					case android.content.DialogInterface.BUTTON_POSITIVE:
						int pin = SecurePreferences.getAdultContentPin();
						String pintext = ((android.widget.EditText) v.findViewById(R.id.pininput)).getText()
                                .toString();
						if (pintext.length() > 0 && Integer.valueOf(pintext) == pin) {
//                            FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Pin");
							positiveButtonlistener.onClick(dialog, which);
						} else {
//                            FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Wrong_Pin");
							android.widget.Toast.makeText(context, context.getString(R.string.adult_pin_wrong),
                                    android.widget.Toast.LENGTH_SHORT)
									.show();
							dialogRequestMaturepin(context, positiveButtonlistener).show();
						}
						break;
					case android.content.DialogInterface.BUTTON_NEGATIVE:
						positiveButtonlistener.onClick(dialog, which);
						break;
				}
			}
		};

		/*
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context).setMessage(R.string
                .request_adult_pin)
				.setView(v)
				.setPositiveButton(android.R.string.ok, onClickListener)
				.setNegativeButton(android.R.string.cancel, onClickListener);
		return builder.create();
		*/

		AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
		dialog.setMessage(R.string.request_adult_pin);
		dialog.setPositiveButton(android.R.string.ok, view -> {
			onClickListener.onClick(dialog.getCreatedDialog(), DialogInterface.BUTTON_POSITIVE);
			dialog.dismiss();
		});
		dialog.setNegativeButton(android.R.string.cancel, view -> {
			onClickListener.onClick(dialog.getCreatedDialog(), DialogInterface.BUTTON_POSITIVE);
			dialog.dismiss();
		});
		return dialog.getCreatedDialog();
	}

	private static android.app.Dialog dialogAsk21(final android.content.Context c, final android.content.DialogInterface.OnClickListener
			positiveButtonlistener) {

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

		AndroidBasicDialog dialog = AndroidBasicDialog.build(c);
		dialog.setMessage(R.string.are_you_adult);
		dialog.setPositiveButton(R.string.yes, v -> {
			Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : UNLOCK ADULT CONTENT");
			Analytics.AdultContent.unlock();
			positiveButtonlistener.onClick(dialog.getCreatedDialog(), DialogInterface.BUTTON_POSITIVE);
			dialog.dismiss();
		});
		dialog.setNegativeButton(R.string.no, v -> {
			dialog.dismiss();
		});
		return dialog.getCreatedDialog();

		// FIXME: 16/08/16 sithengineer use the next line instead
		//return GenericDialogs.createGenericYesNoCancelMessage(c, "", c.getString(R.string.are_you_adult));
	}

	private static android.app.Dialog dialogAsk21(final android.content.Context c, final android.content.DialogInterface.OnClickListener
			positiveButtonlistener, DialogInterface.OnCancelListener cancelListener) {
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

		AndroidBasicDialog dialog = AndroidBasicDialog.build(c);
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

	public static android.app.Dialog buildAreYouAdultDialog(final android.content.Context c, final android.content.DialogInterface.OnClickListener
			positiveButtonlistener) {
		int pin = SecurePreferences.getAdultContentPin();
		if (pin == -1) {
			return dialogAsk21(c, positiveButtonlistener);
		} else {
			return dialogRequestMaturepin(c, positiveButtonlistener);
		}
	}

	public static android.app.Dialog buildAreYouAdultDialog(final android.content.Context c, final android.content.DialogInterface.OnClickListener
			positiveButtonlistener, DialogInterface.OnCancelListener cancelListener) {
		int pin = SecurePreferences.getAdultContentPin();
		if (pin == -1) {
			return dialogAsk21(c, positiveButtonlistener, cancelListener);
		} else {
			return dialogRequestMaturepin(c, positiveButtonlistener);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@NonNull
	@Override
	public android.app.Dialog onCreateDialog(android.os.Bundle savedInstanceState) {

		return buildAreYouAdultDialog(getActivity(), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {

				if (which == android.content.DialogInterface.BUTTON_POSITIVE) {
					SecurePreferences.setAdultSwitch(true);
				}
			}
		});
	}
}
