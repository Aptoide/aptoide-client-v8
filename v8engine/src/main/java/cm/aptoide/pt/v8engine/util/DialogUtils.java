/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.PostReviewRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import rx.functions.Action0;

/**
 * Created by sithengineer on 25/08/16.
 */
public class DialogUtils {

	private static final String TAG = DialogUtils.class.getSimpleName();
	private static final Locale LOCALE = Locale.getDefault();

	public static void showRateDialog(@NonNull Activity activity, @NonNull String appName, @NonNull String packageName, @Nullable String storeName, @Nullable
	Action0 onPositiveCallback) {

		if (!AptoideAccountManager.isLoggedIn()) {
			ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
				AptoideAccountManager.openAccountManager(activity, false);
			});

			return;
		}

		final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_rate_app, null);

		final TextView titleTextView = (TextView) view.findViewById(R.id.title);
		final AppCompatRatingBar reviewRatingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
		final TextInputLayout titleTextInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_title);
		final TextInputLayout reviewTextInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_review);
		final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
		final Button rateBtn = (Button) view.findViewById(R.id.rate_button);

		titleTextView.setText(String.format(LOCALE, activity.getString(R.string.rate_app), appName));

		AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
		AlertDialog dialog = builder.create();

		cancelBtn.setOnClickListener(v -> dialog.dismiss());
		rateBtn.setOnClickListener(v -> {

			AptoideUtils.SystemU.hideKeyboard(activity);

			final String reviewTitle = titleTextInputLayout.getEditText().getText().toString();
			final String reviewText = reviewTextInputLayout.getEditText().getText().toString();
			final int reviewRating = Math.round(reviewRatingBar.getRating());

			if (TextUtils.isEmpty(reviewTitle)) {
				titleTextInputLayout.setError(AptoideUtils.StringU.getResString(R.string.error_MARG_107));
				return;
			}

			titleTextInputLayout.setErrorEnabled(false);
			dialog.dismiss();

			dialog.dismiss();

			final SuccessRequestListener<BaseV7Response> successRequestListener = response -> {
				if (response.isOk()) {
					Logger.d(TAG, "review added");
					ShowMessage.asSnack(activity, R.string.review_success);
					ManagerPreferences.setForceServerRefreshFlag(true);
					if (onPositiveCallback != null) {
						onPositiveCallback.call();
					}
				} else {
					ShowMessage.asSnack(activity, R.string.error_occured);
				}
			};

			final ErrorRequestListener errorRequestListener = e -> {
				Logger.e(TAG, e);
				ShowMessage.asSnack(activity, R.string.error_occured);
			};

			if (storeName != null) {
				PostReviewRequest.of(storeName, packageName, reviewTitle, reviewText, reviewRating).execute(successRequestListener, errorRequestListener);
			} else {
				PostReviewRequest.of(packageName, reviewTitle, reviewText, reviewRating).execute(successRequestListener, errorRequestListener);
			}
		});

		// create and show rating dialog
		dialog.show();
	}
}
