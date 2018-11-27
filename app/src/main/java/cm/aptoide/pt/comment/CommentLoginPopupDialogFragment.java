package cm.aptoide.pt.comment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.ErrorsMapper;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Arrays;
import javax.inject.Inject;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiagopedrinho on 22/11/2018.
 */

public class CommentLoginPopupDialogFragment extends DialogFragment
    implements CommentLoginPopupDialogView {

  @Inject AccountAnalytics accountAnalytics;
  @Inject NotLoggedInShareAnalytics analytics;
  @Inject AppNavigator appNavigator;
  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private GoogleApiAvailability apiAvailability;
  private RxAlertDialog facebookEmailRequiredDialog;
  private Dialog errorDialog;
  private Button moreOptionsButton;
  private Button cancelButton;
  private Button facebookLoginButton;
  private Button googleLoginButton;
  private ProgressDialog progressDialog;
  private AptoideAccountManager accountManager;
  private int requestCode;
  private ThrowableToStringMapper errorMapper;

  private CommentLoginPopupDialogPresenter presenter;

  public static CommentLoginPopupDialogFragment newInstance() {
    CommentLoginPopupDialogFragment fragment = new CommentLoginPopupDialogFragment();
    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    requestCode = getArguments().getInt(FragmentNavigator.REQUEST_CODE_EXTRA);
    errorMapper = new AccountErrorMapper(getContext(), new ErrorsMapper());
    presenter = new CommentLoginPopupDialogPresenter(this, new CompositeSubscription(),
        CrashReport.getInstance(), accountManager,
        ((ActivityResultNavigator) getContext()).getAccountNavigator(),
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), requestCode, errorMapper,
        analytics);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.comment_login_popup_dialog, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    moreOptionsButton = (Button) view.findViewById(R.id.comment_login_more_options_button);
    cancelButton = (Button) view.findViewById(R.id.comment_login_cancel_button);
    facebookLoginButton = (Button) view.findViewById(R.id.comment_login_fb_login_button);
    googleLoginButton = (Button) view.findViewById(R.id.comment_login_google_login_button);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
    cancelButton.setOnClickListener(click -> dismiss());

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    presenter.present();
  }

  public void onResume() {
    super.onResume();

    Window window = getDialog().getWindow();
    Point size = new Point();

    Display display = window.getWindowManager()
        .getDefaultDisplay();
    display.getSize(size);

    int width = size.x;

    window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.dispose();
    presenter = null;
  }

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookLoginButton)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.CONNECT_FACEBOOK,
            getStartupClickOrigin()));
  }

  @Override public Observable<Void> googleSignUpEvent() {
    return RxView.clicks(googleLoginButton)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.CONNECT_GOOGLE,
            getStartupClickOrigin()));
  }

  @Override public Observable<Void> facebookSignUpWithRequiredPermissionsInEvent() {
    return facebookEmailRequiredDialog.positiveClicks()
        .map(dialog -> null);
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

  @Override public void showError() {

  }

  @Override public void showConnectionError(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      showResolution(connectionResult);
    } else {
      showConnectionErrorMessage(connectionResult.getErrorCode());
    }
  }

  private void showResolution(ConnectionResult result) {
    try {
      result.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    } catch (IntentSender.SendIntentException e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  private void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }
    errorDialog = apiAvailability.getErrorDialog(getActivity(), errorCode,
        RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    errorDialog.show();
  }

  private AccountAnalytics.StartupClickOrigin getStartupClickOrigin() {
    return AccountAnalytics.StartupClickOrigin.COMMENT_NOT_LOGGED_IN_POPUP_DIALOG;
  }
}
