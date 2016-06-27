/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/06/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.R;

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

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context).setMessage(R.string
                .request_adult_pin)
				.setView(v)
				.setPositiveButton(android.R.string.ok, onClickListener)
				.setNegativeButton(android.R.string.cancel, onClickListener);
		return builder.create();
	}

	private static android.app.Dialog dialogAsk21(final android.content.Context c, final android.content
            .DialogInterface.OnClickListener positiveButtonlistener) {
		return new android.app.AlertDialog.Builder(c).setMessage(c.getString(R.string.are_you_adult))
				.setPositiveButton(R.string.yes, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
//                        FlurryAgent.logEvent("Dialog_Adult_Content_Confirmed_More_Than_21_Years_Old");
						positiveButtonlistener.onClick(dialog, which);
						//Analytics.AdultContent.unlock();
					}
				})
				.setNegativeButton(R.string.no, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						dialog.cancel();
						//Analytics.AdultContent.lock();
					}
				})
				.create();
	}

	public static android.app.Dialog buildAreYouAdultDialog(final android.content.Context c, final android.content
            .DialogInterface.OnClickListener positiveButtonlistener) {
		int pin = SecurePreferences.getAdultContentPin();
		if (pin == -1) {
			return dialogAsk21(c, positiveButtonlistener);
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
