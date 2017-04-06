package cm.aptoide.pt.v8engine.view.updates.installed;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.PostReviewRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.dialog.DialogUtils;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({ InstalledAppDisplayable.class }) public class InstalledAppWidget
    extends Widget<InstalledAppDisplayable> {

  private static final Locale LOCALE = Locale.getDefault();
  private static final String TAG = InstalledAppWidget.class.getSimpleName();
  private AptoideAccountManager accountManager;
  private DialogUtils dialogUtils;

  private TextView labelTextView;
  private TextView verNameTextView;
  private ImageView iconImageView;
  private View installedItemFrame;
  private ViewGroup createReviewLayout;

  private String appName;
  private String packageName;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyInterceptor;

  public InstalledAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    labelTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    verNameTextView = (TextView) itemView.findViewById(R.id.app_version);
    installedItemFrame = itemView.findViewById(R.id.installedItemFrame);
    createReviewLayout = (ViewGroup) itemView.findViewById(R.id.reviewButtonLayout);
  }

  @Override public void bindView(InstalledAppDisplayable displayable) {
    Installed pojo = displayable.getPojo();

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    this.bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();

    final AccountNavigator accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    this.accountNavigator = accountNavigator;
    dialogUtils = new DialogUtils(accountManager, accountNavigator, bodyInterceptor);
    appName = pojo.getName();
    packageName = pojo.getPackageName();

    labelTextView.setText(pojo.getName());
    verNameTextView.setText(pojo.getVersionName());
    final FragmentActivity context = getContext();
    ImageLoader.with(context).load(pojo.getIcon(), iconImageView);

    installedItemFrame.setOnClickListener(v -> {
      // TODO: 25-05-2016 neuro apagar em principio
    });

    // [AN-512] - Create Review on Installed Apps List
    // only show create review if store info is associated with this install
    final String storeName = pojo.getStoreName();
    if (!TextUtils.isEmpty(storeName)) {
      createReviewLayout.setVisibility(View.VISIBLE);
      compositeSubscription.add(RxView.clicks(createReviewLayout)
          .flatMap(__ -> dialogUtils.showRateDialog(getContext(), appName, packageName, storeName))
          .subscribe(__ -> Analytics.Updates.createReview(),
              err -> CrashReport.getInstance().log(err)));
    } else {
      createReviewLayout.setVisibility(View.GONE);
    }
  }

  private void showRateDialog() {
    final Context ctx = getContext();
    final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_app, null);

    final TextView titleTextView = (TextView) view.findViewById(R.id.title);
    final AppCompatRatingBar reviewRatingBar =
        (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
    final TextInputLayout titleTextInputLayout =
        (TextInputLayout) view.findViewById(R.id.input_layout_title);
    final TextInputLayout reviewTextInputLayout =
        (TextInputLayout) view.findViewById(R.id.input_layout_review);
    final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
    final Button rateBtn = (Button) view.findViewById(R.id.rate_button);

    titleTextView.setText(String.format(LOCALE, ctx.getString(R.string.rate_app), appName));

    AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
    AlertDialog dialog = builder.create();

    cancelBtn.setOnClickListener(v -> dialog.dismiss());
    rateBtn.setOnClickListener(v -> {

      AptoideUtils.SystemU.hideKeyboard(getContext());

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
      PostReviewRequest.of(packageName, reviewTitle, reviewText, reviewRating, bodyInterceptor)
          .execute(response -> {
            if (response.isOk()) {
              Logger.d(TAG, "review added");
              ShowMessage.asSnack(labelTextView, R.string.review_success);
              ManagerPreferences.setForceServerRefreshFlag(true);
            } else {
              ShowMessage.asSnack(labelTextView, R.string.error_occured);
            }
          }, e -> {
            CrashReport.getInstance().log(e);
            ShowMessage.asSnack(labelTextView, R.string.error_occured);
          });
    });

    // create and show rating dialog
    dialog.show();
  }
}
