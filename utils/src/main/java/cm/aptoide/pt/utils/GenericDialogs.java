/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cm.aptoide.pt.dialog.AndroidBasicDialog;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by trinkes on 5/9/16. <li>{@link #createGenericYesNoCancelMessage(Context, String,
 * String)}</li> <li>{@link #createGenericOkCancelMessage(Context, String, String)}</li> <li>{@link
 * #createGenericPleaseWaitDialog(Context)}</li>
 */
public class GenericDialogs {

	/**
	 * Show an AlertDialog with the {@code title} and the {@code message}. The Alert dialog has an
	 * "yes" button and a "no" button.
	 *
	 * @param title   Title to apply on AlertDialog
	 * @param message Message to asSnack on AlertDialog
	 * @return A Observable that shows the dialog when subscribed and return the action made by
	 * user. This action is represented by EResponse
	 * @see EResponse
	 */
	public static Observable<EResponse> createGenericYesNoCancelMessage(@NonNull Context context,
																		@Nullable String title,
																		@Nullable String message) {
		return Observable.create((Subscriber<? super EResponse> subscriber) -> {
			/*
			final AlertDialog ad = new AlertDialog.Builder(context).setTitle(title)
					.setMessage(message)
					.setPositiveButton(android.R.string.yes, (dialog, which) -> {
						subscriber.onNext(EResponse.YES);
						subscriber.onCompleted();
					})
					.setNegativeButton(android.R.string.no, (dialog, which) -> {
						subscriber.onNext(EResponse.NO);
						subscriber.onCompleted();
					})
					.setOnCancelListener(dialog -> {
						subscriber.onNext(EResponse.CANCEL);
						subscriber.onCompleted();
					})
					.create();
			// cleaning up
			subscriber.add(Subscriptions.create(ad::dismiss));
			ad.show();
			*/

			final AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
			dialog.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, v -> {
				dialog.dismiss();
				subscriber.onNext(EResponse.YES);
				subscriber.onCompleted();
			}).setNegativeButton(android.R.string.no, v -> {
				dialog.dismiss();
				subscriber.onNext(EResponse.NO);
				subscriber.onCompleted();
			}).setOnCancelListener(() -> {
				subscriber.onNext(EResponse.CANCEL);
				subscriber.onCompleted();
			});
			// cleaning up
			subscriber.add(Subscriptions.create(dialog::dismiss));
			dialog.show();
		});
	}

	/**
	 * Show an AlertDialog with the {@code title} and the {@code message}. The Alert dialog has an
	 * "ok" button.
	 *
	 * @param title   Title to apply on AlertDialog
	 * @param message Message to asSnack on AlertDialog
	 * @return A Observable that shows the dialog when subscribed and return the action made by
	 * user. This action is represented by EResponse
	 * @see EResponse
	 */
	public static Observable<EResponse> createGenericOkCancelMessage(Context context, String
			title, String message) {
		return Observable.create((Subscriber<? super EResponse> subscriber) -> {
			/*
			final AlertDialog ad = new AlertDialog.Builder(context).setTitle(title)
					.setMessage(message)
					.setPositiveButton(android.R.string.ok, (dialog, which) -> {
						subscriber.onNext(EResponse.YES);
						subscriber.onCompleted();
					})
					.setOnCancelListener(dialog -> {
						subscriber.onNext(EResponse.CANCEL);
						subscriber.onCompleted();
					})
					.create();
			// cleaning up
			subscriber.add(Subscriptions.create(ad::dismiss));
			ad.show();
			*/

			final AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
			dialog.setTitle(title)
					.setMessage(message)
					.setPositiveButton(android.R.string.ok, v -> {
						subscriber.onNext(EResponse.YES);
						subscriber.onCompleted();
				dialog.dismiss();
			}).setNegativeButton(android.R.string.cancel, v -> {
				dialog.dismiss();
				subscriber.onNext(EResponse.CANCEL);
				subscriber.onCompleted();
			});
			// cleaning up
			subscriber.add(Subscriptions.create(dialog::dismiss));
			dialog.show();
		});
	}

	/**
	 * Creates an endless progressDialog to be shown when user is waiting for something
	 *
	 * @return A ProgressDialog with a please wait message
	 */
	public static ProgressDialog createGenericPleaseWaitDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	/**
	 * Represents the action made by user on the dialog. <li>{@link #YES}</li> <li>{@link #NO}</li>
	 * <li>{@link #CANCEL}</li>
	 */
	public enum EResponse {

		/**
		 * Used when yes/ok button is pressed
		 */
		YES,
		/**
		 * Used when no/cancel button is pressed
		 */
		NO,
		/**
		 * Used when user cancels the dialog by pressing back or clicking out of the dialog
		 */
		CANCEL
	}
}
