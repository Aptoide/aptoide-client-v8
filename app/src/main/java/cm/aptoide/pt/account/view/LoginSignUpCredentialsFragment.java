package cm.aptoide.pt.account.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.Nullable;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.login.SendMagicLinkView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.magiclink.MagicLinkView;
import cm.aptoide.pt.account.view.magiclink.SendMagicLinkPresenter;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.presenter.LoginSignupCredentialsFlavorPresenter;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class LoginSignUpCredentialsFragment extends GooglePlayServicesFragment
    implements LoginSignUpCredentialsView, MagicLinkView, NotBottomNavigationView {

  public static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  public static final String CLEAN_BACK_STACK = "clean_back_stack";
  public static final String HAS_MAGIC_LINK_ERROR = "has_magic_link_error";
  public static final String MAGIC_LINK_ERROR_MESSAGE = "magic_link_error_message";

  private static final String USERNAME_KEY = "username_key";
  @Inject LoginSignupCredentialsFlavorPresenter presenter;
  @Inject SendMagicLinkPresenter sendMagicLinkPresenter;
  @Inject ScreenOrientationManager orientationManager;
  @Inject AccountAnalytics accountAnalytics;
  @Inject @Named("marketName") String marketName;
  @Inject ThemeManager themeManager;
  private ProgressDialog progressDialog;
  private RxAlertDialog facebookEmailRequiredDialog;
  private Button googleLoginButton;
  private View facebookLoginButton;
  private View loginSignupSelectionArea;
  private Button connectWithEmailButton;
  private SendMagicLinkView sendMagicLinkView;
  private View socialLoginArea;
  private View rootView;

  public static LoginSignUpCredentialsFragment newInstance(boolean dismissToNavigateToMainView,
      boolean cleanBackStack) {
    return newInstance(dismissToNavigateToMainView, cleanBackStack, false, "");
  }

  public static LoginSignUpCredentialsFragment newInstance(boolean dismissToNavigateToMainView,
      boolean cleanBackStack, boolean hasMagicLinkError, String magicLinkErrorMessage) {
    final LoginSignUpCredentialsFragment fragment = new LoginSignUpCredentialsFragment();

    final Bundle bundle = new Bundle();
    bundle.putBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW, dismissToNavigateToMainView);
    bundle.putBoolean(CLEAN_BACK_STACK, cleanBackStack);
    bundle.putBoolean(HAS_MAGIC_LINK_ERROR, hasMagicLinkError);
    bundle.putString(MAGIC_LINK_ERROR_MESSAGE, magicLinkErrorMessage);
    fragment.setArguments(bundle);

    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rootView = getActivity().findViewById(android.R.id.content);

    googleLoginButton = view.findViewById(R.id.google_login_button);

    facebookLoginButton = view.findViewById(R.id.fb_login_button);

    loginSignupSelectionArea = view.findViewById(R.id.login_signup_selection_layout);
    sendMagicLinkView = view.findViewById(R.id.send_magic_link_view);
    socialLoginArea = view.findViewById(R.id.social_login_area);
    connectWithEmailButton = view.findViewById(R.id.show_login_with_aptoide_area);

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext(), themeManager).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);

    attachPresenter(new CompositePresenter(Arrays.asList(presenter, sendMagicLinkPresenter)));
    registerClickHandler(presenter);
  }

  @Override public void onDestroyView() {
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    unregisterClickHandler(presenter);
    unlockScreenRotation();
    sendMagicLinkView = null;
    super.onDestroyView();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_login_sign_up_credentials, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override public Observable<Void> showAptoideLoginAreaClick() {
    return RxView.clicks(connectWithEmailButton);
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

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookLoginButton)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.CONNECT_FACEBOOK,
            getStartupClickOrigin()));
  }

  @Override public void showAptoideLoginArea() {
    setAptoideLoginAreaVisible();
    hideSocialLoginArea();
  }

  @Override public void showMagicLinkError(String error) {
    sendMagicLinkView.setState(new SendMagicLinkView.State.Error(error, false));
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(String message) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showFacebookLogin() {
    facebookLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void showFacebookPermissionsRequiredError(Throwable throwable) {
    if (!facebookEmailRequiredDialog.isShowing()) {
      facebookEmailRequiredDialog.show();
    }
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void dismiss() {
    getActivity().finish();
  }

  @Override public void showGoogleLogin() {
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public boolean tryCloseLoginBottomSheet(boolean shouldShowTCandPP) {
    if (sendMagicLinkView.getVisibility() == View.VISIBLE) {
      sendMagicLinkView.setVisibility(View.GONE);
      loginSignupSelectionArea.setVisibility(View.VISIBLE);
      socialLoginArea.setVisibility(View.VISIBLE);
      return true;
    }
    return false;
  }

  @Override public Context getApplicationContext() {
    return getActivity().getApplicationContext();
  }

  @Override public void lockScreenRotation() {
    orientationManager.lock();
  }

  @Override public void unlockScreenRotation() {
    orientationManager.unlock();
  }

  @Override public void setCobrandText() {

  }

  private void hideSocialLoginArea() {
    socialLoginArea.setVisibility(View.GONE);
  }

  private AccountAnalytics.StartupClickOrigin getStartupClickOrigin() {
    return AccountAnalytics.StartupClickOrigin.MAIN;
  }

  private void setAptoideLoginAreaVisible() {
    sendMagicLinkView.setVisibility(View.VISIBLE);
    loginSignupSelectionArea.setVisibility(View.GONE);
  }

  @NotNull @Override public Observable<String> getMagicLinkClick() {
    return sendMagicLinkView.getMagicLinkSubmit();
  }

  @Override public void setInitialState() {
    sendMagicLinkView.setState(SendMagicLinkView.State.Initial.INSTANCE);
  }

  @Override public void removeTextFieldError() {
    sendMagicLinkView.resetTextFieldError();
  }

  @Override public void setEmailInvalidError() {
    sendMagicLinkView.setState(
        new SendMagicLinkView.State.Error(getString(R.string.login_error_invalid_email), true));
  }

  @Override public void setLoadingScreen() {
    showLoading();
  }

  @Override public void removeLoadingScreen() {
    hideKeyboard();
    hideLoading();
  }

  @NotNull @Override public Observable<String> getEmailTextChangeEvent() {
    return sendMagicLinkView.getEmailChangeEvent();
  }

  @Override public void showUnknownError() {
    showMagicLinkError(getString(R.string.all_message_general_error));
  }

  @NotNull @Override public Observable<Void> getSecureLoginTextClick() {
    return sendMagicLinkView.getSecureLoginTextClick();
  }
}
