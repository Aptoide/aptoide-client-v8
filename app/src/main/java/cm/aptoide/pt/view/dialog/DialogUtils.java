/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.PostReviewRequest;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Use specific {@link DialogFragment}s for this use cases. Avoid adding more dialog builder methods
 * to this class.
 */
@Deprecated public class DialogUtils {

  private static final String TAG = DialogUtils.class.getSimpleName();
  private final Locale LOCALE = Locale.getDefault();
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final InstalledRepository installedRepository;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final String marketName;
  private final MarketResourceFormatter marketResourceFormatter;

  public DialogUtils(AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, InstalledRepository installedRepository,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      String marketName, MarketResourceFormatter marketResourceFormatter) {
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.installedRepository = installedRepository;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.marketName = marketName;
    this.marketResourceFormatter = marketResourceFormatter;
  }

  public Observable<GenericDialogs.EResponse> showRateDialog(@NonNull Activity activity,
      @NonNull String appName, @NonNull String packageName, @Nullable String storeName) {

    return Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {

      if (!accountManager.isLoggedIn()) {
        ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
            snackView -> {
              accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.RATE_DIALOG);
            }, Snackbar.LENGTH_SHORT);
        subscriber.onNext(GenericDialogs.EResponse.CANCEL);
        subscriber.onCompleted();
        return;
      }

      final View view = LayoutInflater.from(activity)
          .inflate(R.layout.dialog_rate_app, null);

      final TextView titleTextView = (TextView) view.findViewById(R.id.title);
      final AppCompatRatingBar reviewRatingBar =
          (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
      final TextInputLayout reviewTextInputLayout =
          (TextInputLayout) view.findViewById(R.id.input_layout_review);
      final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
      final Button rateBtn = (Button) view.findViewById(R.id.rate_button);

      final TextView highlightedReviewsExplained_1 =
          (TextView) view.findViewById(R.id.highlighted_reviews_explanation_1);
      final TextView highlightedReviewsExplained_2 =
          (TextView) view.findViewById(R.id.highlighted_reviews_explanation_2);
      final TextView highlightedReviewsExplained_3 =
          (TextView) view.findViewById(R.id.highlighted_reviews_explanation_3);
      final TextView highlightedReviewsExplained_4 =
          (TextView) view.findViewById(R.id.highlighted_reviews_explanation_4);

      titleTextView.setText(String.format(LOCALE, activity.getString(R.string.rate_app), appName));

      final AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
      final AlertDialog dialog = builder.create();
      subscriber.add(Subscriptions.create(() -> {
        if (dialog != null && dialog.isShowing()) {
          dialog.dismiss();
        }
      }));

      cancelBtn.setOnClickListener(v -> {
        subscriber.onNext(GenericDialogs.EResponse.CANCEL);
        subscriber.onCompleted();
      });

      reviewRatingBar.setOnRatingBarChangeListener(
          (ratingBar, v, b) -> ((TextView) view.findViewById(R.id.tap_to_rate)).setTextColor(
              activity.getResources()
                  .getColor(R.color.grey_fog_dark)));
      rateBtn.setOnClickListener(v -> {

        AptoideUtils.SystemU.hideKeyboard(activity);

        final String reviewText = reviewTextInputLayout.getEditText()
            .getText()
            .toString();
        final int reviewRating = Math.round(reviewRatingBar.getRating());
        if (reviewRating == 0) {
          ((TextView) view.findViewById(R.id.tap_to_rate)).setTextColor(activity.getResources()
              .getColor(R.color.red));
          ShowMessage.asSnack(activity, "Please choose a rating");
          return;
        }
        dialog.dismiss();

        // WS success listener
        final SuccessRequestListener<BaseV7Response> successRequestListener = response -> {
          if (response.isOk()) {
            Logger.getInstance()
                .d(TAG, "review added");
            ShowMessage.asSnack(activity, R.string.review_success);
            ManagerPreferences.setForceServerRefreshFlag(true, sharedPreferences);
            subscriber.onNext(GenericDialogs.EResponse.YES);
            subscriber.onCompleted();
          } else {
            ShowMessage.asSnack(activity, R.string.error_occured);
            subscriber.onNext(GenericDialogs.EResponse.CANCEL);
            subscriber.onCompleted();
          }
        };

        // WS error listener
        final ErrorRequestListener errorRequestListener = e -> {
          CrashReport.getInstance()
              .log(e);
          ShowMessage.asSnack(activity, R.string.error_occured);
          subscriber.onNext(GenericDialogs.EResponse.CANCEL);
          subscriber.onCompleted();
        };

        // WS call
        if (storeName != null) {

          PostReviewRequest.of(storeName, packageName, reviewText, reviewRating, bodyInterceptor,
              httpClient, converterFactory, isAppInstalled(packageName), tokenInvalidator,
              sharedPreferences)
              .execute(successRequestListener, errorRequestListener);
        } else {
          PostReviewRequest.of(packageName, reviewText, reviewRating, bodyInterceptor, httpClient,
              converterFactory, isAppInstalled(packageName), tokenInvalidator, sharedPreferences)
              .execute(successRequestListener, errorRequestListener);
        }
      });

      highlightedReviewsExplained_1.setText(activity.getResources()
          .getString(R.string.reviewappview_highlighted_reviews_explanation_1));

      setBulletText(highlightedReviewsExplained_2, activity.getResources()
          .getString(R.string.reviewappview_highlighted_reviews_explanation_2));
      setBulletText(highlightedReviewsExplained_3, activity.getResources()
          .getString(R.string.reviewappview_highlighted_reviews_explanation_3));
      setBulletText(highlightedReviewsExplained_4,
          marketResourceFormatter.formatString(view.getContext(),
              R.string.reviewappview_highlighted_reviews_explanation_4));

      // create and show rating dialog
      dialog.show();
    });
  }

  public void setBulletText(TextView textView, String text) {
    SpannableString spannable = new SpannableString(text);
    spannable.setSpan(new BulletSpan(16, resources.getColor(R.color.default_orange_gradient_end)),
        0, text.length(), 0);

    textView.setText(spannable);
  }

  private boolean isAppInstalled(@NonNull String packageName) {
    return installedRepository.contains(packageName);
  }

  public void showRateDialog(@NonNull Activity activity, @NonNull String appName,
      @NonNull String packageName, @Nullable String storeName,
      @Nullable Action0 onPositiveCallback) {

    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.RATE_DIALOG);
          }, Snackbar.LENGTH_SHORT);

      return;
    }

    final View view = LayoutInflater.from(activity)
        .inflate(R.layout.dialog_rate_app, null);

    final TextView titleTextView = (TextView) view.findViewById(R.id.title);
    final AppCompatRatingBar reviewRatingBar =
        (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
    final TextInputLayout reviewTextInputLayout =
        (TextInputLayout) view.findViewById(R.id.input_layout_review);
    final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
    final Button rateBtn = (Button) view.findViewById(R.id.rate_button);

    titleTextView.setText(String.format(LOCALE, activity.getString(R.string.rate_app), appName));

    AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
    AlertDialog dialog = builder.create();

    cancelBtn.setOnClickListener(v -> dialog.dismiss());
    rateBtn.setOnClickListener(v -> {

      AptoideUtils.SystemU.hideKeyboard(activity);

      final String reviewText = reviewTextInputLayout.getEditText()
          .getText()
          .toString();
      final int reviewRating = Math.round(reviewRatingBar.getRating());

      dialog.dismiss();

      final SuccessRequestListener<BaseV7Response> successRequestListener = response -> {
        if (response.isOk()) {
          Logger.getInstance()
              .d(TAG, "review added");
          ShowMessage.asSnack(activity, R.string.review_success);
          ManagerPreferences.setForceServerRefreshFlag(true, sharedPreferences);
          if (onPositiveCallback != null) {
            onPositiveCallback.call();
          }
        } else {
          ShowMessage.asSnack(activity, R.string.error_occured);
        }
      };

      final ErrorRequestListener errorRequestListener = e -> {
        CrashReport.getInstance()
            .log(e);
        ShowMessage.asSnack(activity, R.string.error_occured);
      };

      if (storeName != null) {
        PostReviewRequest.of(storeName, packageName, reviewText, reviewRating, bodyInterceptor,
            httpClient, converterFactory, isAppInstalled(packageName), tokenInvalidator,
            sharedPreferences)
            .execute(successRequestListener, errorRequestListener);
      } else {
        PostReviewRequest.of(packageName, reviewText, reviewRating, bodyInterceptor, httpClient,
            converterFactory, isAppInstalled(packageName), tokenInvalidator, sharedPreferences)
            .execute(successRequestListener, errorRequestListener);
      }
    });

    // create and show rating dialog
    dialog.show();
  }
}
