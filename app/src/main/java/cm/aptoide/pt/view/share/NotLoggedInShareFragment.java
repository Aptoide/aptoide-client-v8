package cm.aptoide.pt.view.share;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.FacebookLoginManager;
import cm.aptoide.pt.account.GoogleLoginManager;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.view.account.AccountErrorMapper;
import cm.aptoide.pt.view.account.GoogleLoginFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

public class NotLoggedInShareFragment extends GoogleLoginFragment implements NotLoggedInShareView {

  private static final String APP_NAME = "app_name";
  private static final String APP_ICON = "app_title";
  private static final String APP_RATING = "app_rating";

  private ProgressDialog progressDialog;
  private LoginManager facebookLoginManager;
  private CallbackManager callbackManager;
  private Button facebookLoginButton;
  private Button googleButton;
  private RatingBar appRating;
  private TextView appTitle;
  private ImageView appIcon;
  private View closeButton;
  private PublishRelay<LoginResult> facebookLoginSubject;
  private List<String> facebookRequestedPermissions;
  private ThrowableToStringMapper errorMapper;
  private AlertDialog facebookEmailRequiredDialog;
  private ImageView previewSocialContent;
  private ImageView fakeToolbar;
  private ImageView loginProgressIndicator;

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
    facebookLoginManager = LoginManager.getInstance();
    callbackManager = CallbackManager.Factory.create();
    facebookLoginSubject = PublishRelay.create();
    errorMapper = new AccountErrorMapper(getContext());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.not_logged_in_share, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    facebookLoginButton = (Button) view.findViewById(R.id.not_logged_in_share_facebook_button);
    googleButton = (Button) view.findViewById(R.id.not_logged_in_share_google_button);
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
    facebookRequestedPermissions = Arrays.asList("email", "user_friends");

    RxView.clicks(facebookLoginButton)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(
            __ -> facebookLoginManager.logInWithReadPermissions(NotLoggedInShareFragment.this,
                facebookRequestedPermissions));

    facebookEmailRequiredDialog = new AlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button, (dialog, which) -> {
          facebookLoginManager.logInWithReadPermissions(this, Arrays.asList("email"));
        })
        .setNegativeButton(android.R.string.cancel, null)
        .create();

    final ColorMatrixColorFilter zeroSaturationFilter = getColorMatrixColorFilter(0);
    appIcon.setColorFilter(zeroSaturationFilter);
    previewSocialContent.setColorFilter(zeroSaturationFilter);
    fakeToolbar.setColorFilter(zeroSaturationFilter);
    appRating.getProgressDrawable()
        .setColorFilter(zeroSaturationFilter);
    loginProgressIndicator.setColorFilter(getColorMatrixColorFilter(0.3f));

    ImageLoader.with(getContext())
        .load(getArguments().getString(APP_ICON), appIcon);

    attachPresenter(new NotLoggedInSharePresenter(this,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        CrashReport.getInstance(),
        ((AptoideApplication) getContext().getApplicationContext()).getLoginPreferences(),
        new FacebookLoginManager(
            ((AptoideApplication) getContext().getApplicationContext()).getAccountManager(),
            facebookRequestedPermissions), new GoogleLoginManager(
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager())), null);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void showFacebookLogin() {
    facebookLoginButton.setVisibility(View.VISIBLE);
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(loginResult);
      }

      @Override public void onCancel() {
        showFacebookCancelledError();
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED, Analytics.Account.LoginStatusDetail.CANCEL);
      }

      @Override public void onError(FacebookException error) {
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED,
            Analytics.Account.LoginStatusDetail.SDK_ERROR);
        showFacebookLoginError();
      }
    });
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void showFacebookLoginError() {
    Snackbar.make(getRootView(), R.string.error_occured, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showFacebookCancelledError() {
    Snackbar.make(getRootView(), R.string.facebook_login_cancelled, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public Observable<LoginResult> facebookLoginClick() {
    return facebookLoginSubject;
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(Throwable throwable) {
    Snackbar.make(getRootView(), errorMapper.map(throwable), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override protected Button getGoogleButton() {
    return googleButton;
  }

  @Override public Observable<Void> closeClick() {
    return RxView.clicks(closeButton);
  }

  @Override public void closeFragment() {
    finishWithResult(RESULT_CANCELED);
  }

  @Override public void navigateToMainView() {
    finishWithResult(RESULT_OK);
  }

  @Override public Context getApplicationContext() {
    return getActivity().getApplicationContext();
  }

  private View getRootView() {
    return getActivity().findViewById(android.R.id.content);
  }

  private ColorMatrixColorFilter getColorMatrixColorFilter(float saturation) {
    final ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(saturation);
    return new ColorMatrixColorFilter(matrix);
  }
}