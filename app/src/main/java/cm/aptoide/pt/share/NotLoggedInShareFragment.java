package cm.aptoide.pt.share;

import android.app.ProgressDialog;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.GooglePlayServicesFragment;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import rx.Observable;

public class NotLoggedInShareFragment extends GooglePlayServicesFragment
    implements NotLoggedInShareView {

  private static final String APP_NAME = "app_name";
  private static final String APP_ICON = "app_title";
  private static final String APP_RATING = "app_rating";

  private ProgressDialog progressDialog;
  private Button facebookLoginButton;
  private Button googleLoginButton;
  private RatingBar appRating;
  private TextView appTitle;
  private ImageView appIcon;
  private View closeButton;
  private ThrowableToStringMapper errorMapper;
  private RxAlertDialog facebookEmailRequiredDialog;
  private ImageView previewSocialContent;
  private ImageView fakeToolbar;
  private ImageView loginProgressIndicator;
  private AptoideAccountManager accountManager;
  private int requestCode;
  private View fakeTimeline;
  private PublishRelay<Void> backButtonPress;
  private View outerLayout;

  public static NotLoggedInShareFragment newInstance(GetAppMeta.App app) {
    NotLoggedInShareFragment fragment = new NotLoggedInShareFragment();
    Bundle bundle = new Bundle();
    bundle.putString(APP_NAME, app.getName());
    bundle.putString(APP_ICON, app.getIcon());
    bundle.putFloat(APP_RATING, app.getStats()
        .getRating()
        .getAvg());
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    errorMapper = new AccountErrorMapper(getContext());
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    requestCode = getArguments().getInt(FragmentNavigator.REQUEST_CODE_EXTRA);
    backButtonPress = PublishRelay.create();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.not_logged_in_share, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    facebookLoginButton = (Button) view.findViewById(R.id.not_logged_in_share_facebook_button);
    googleLoginButton = (Button) view.findViewById(R.id.not_logged_in_share_google_button);
    appIcon = (ImageView) view.findViewById(R.id.not_logged_in_app_icon);
    appTitle = (TextView) view.findViewById(R.id.not_logged_int_app_title);
    closeButton = view.findViewById(R.id.not_logged_in_close);
    appRating = (RatingBar) view.findViewById(R.id.not_logged_in_app_rating);
    appTitle.setText(getArguments().getString(APP_NAME));
    appRating.setRating(getArguments().getFloat(APP_RATING));
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
    previewSocialContent = (ImageView) view.findViewById(R.id.not_logged_in_preview_social_content);
    fakeToolbar = (ImageView) view.findViewById(R.id.fake_toolbar);
    loginProgressIndicator = (ImageView) view.findViewById(R.id.login_progress_indicator);
    fakeTimeline = view.findViewById(R.id.fake_timeline);
    outerLayout = view.findViewById(R.id.outer_layout);

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    final ColorMatrixColorFilter zeroSaturationFilter = getColorMatrixColorFilter(0);
    appIcon.setColorFilter(zeroSaturationFilter);
    previewSocialContent.setColorFilter(zeroSaturationFilter);
    fakeToolbar.setColorFilter(zeroSaturationFilter);
    appRating.getProgressDrawable()
        .setColorFilter(zeroSaturationFilter);
    loginProgressIndicator.setColorFilter(getColorMatrixColorFilter(0.3f));

    ImageLoader.with(getContext())
        .load(getArguments().getString(APP_ICON), appIcon);
    registerClickHandler(() -> {
      backButtonPress.call(null);
      return true;
    });

    attachPresenter(new NotLoggedInSharePresenter(this,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            CrashReport.getInstance(), accountManager,
            ((ActivityResultNavigator) getContext()).getAccountNavigator(),
            Arrays.asList("email", "user_friends"), Arrays.asList("email"), requestCode, errorMapper, ((AptoideApplication) getContext().getApplicationContext()).getNotLoggedInShareAnalytics()));
  }

  private Analytics.Account.StartupClickOrigin getStartupClickOrigin() {
    return Analytics.Account.StartupClickOrigin.NOT_LOGGED_IN_DIALOG;
  }

  private View getRootView() {
    return getActivity().findViewById(android.R.id.content);
  }

  @Override public Observable<Void> closeEvent() {
    return RxView.clicks(closeButton);
  }

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookLoginButton)
        .doOnNext(__ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.CONNECT_FACEBOOK,
            getStartupClickOrigin()));
  }

  @Override public Observable<Void> googleSignUpEvent() {
    return RxView.clicks(googleLoginButton)
        .doOnNext(__ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.CONNECT_GOOGLE,
            getStartupClickOrigin()));
  }

  @Override public Observable<Void> facebookSignUpWithRequiredPermissionsInEvent() {
    return facebookEmailRequiredDialog.positiveClicks()
        .map(dialog -> null);
  }

  @Override public void showError(String message) {
    Snackbar.make(getRootView(), message, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showFacebookPermissionsRequiredError(Throwable throwable) {
    if (!facebookEmailRequiredDialog.isShowing()) {
      facebookEmailRequiredDialog.show();
    }
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showFacebookLogin() {
    facebookLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void showGoogleLogin() {
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public Observable<Void> getFakeToolbarClick() {
    return RxView.clicks(fakeToolbar);
  }

  @Override public Observable<Void> getFakeTimelineClick() {
    return RxView.clicks(fakeTimeline);
  }

  @Override public Observable<Void> backEvent() {
    return backButtonPress;
  }

  @Override public Observable<Void> getOutsideClick() {
    return RxView.clicks(outerLayout);
  }

  private ColorMatrixColorFilter getColorMatrixColorFilter(float saturation) {
    final ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(saturation);
    return new ColorMatrixColorFilter(matrix);
  }
}