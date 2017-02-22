package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.OAuthException;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.fragment.GoogleLoginFragment;
import cm.aptoide.pt.v8engine.presenter.LoginSignUpPresenter;
import cm.aptoide.pt.v8engine.view.LoginSignUpView;
import cm.aptoide.pt.v8engine.viewModel.AptoideAccountViewModel;
import cm.aptoide.pt.v8engine.viewModel.FacebookAccountViewModel;
import cm.aptoide.pt.v8engine.viewModel.GoogleAccountViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

public class LoginSignUpFragment extends GoogleLoginFragment implements LoginSignUpView {

  private static final String TAG = LoginSignUpFragment.class.getName();

  private static final String SKIP_BUTTON = "USE_SKIP_BUTTON";
  private static final String CLEAR_BACK_STACK = "CLEAR_BACK_STACK";
  private static final String IS_LOGIN = "IS_LOGIN";

  private ProgressDialog progressDialog;
  private CallbackManager callbackManager;

  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;

  private LoginManager facebookLoginManager;
  private AlertDialog facebookEmailRequiredDialog;
  private List<String> facebookRequestedPermissions;
  private Snackbar successSnackbar;

  private SignInButton googleLoginButton;
  private LoginButton facebookLoginButton;
  private Button hideShowAptoidePasswordButton;
  private View loginArea;
  private View signUpArea;
  private EditText aptoideEmailEditText;
  private EditText aptoidePasswordEditText;
  private TextView forgotPasswordButton;
  private Button buttonSignUp;
  private Button buttonLogin;

  private boolean isPasswordVisible = false;
  private boolean useSkipButton;
  private boolean isLogin;

  public static LoginSignUpFragment newInstance() {
    return newInstance(true);
  }

  public static LoginSignUpFragment newInstance(boolean isLogin) {
    Fragment f = new LoginSignUpFragment();
    Bundle args = new Bundle();
    args.putBoolean(LoginSignUpFragment.IS_LOGIN, isLogin);
    f.setArguments(args);
    return new LoginSignUpFragment();
  }

  public static Fragment newInstance(boolean useSkip, boolean isLogin) {
    Fragment f = new LoginSignUpFragment();
    Bundle args = new Bundle();
    args.putBoolean(LoginSignUpFragment.SKIP_BUTTON, useSkip);
    args.putBoolean(LoginSignUpFragment.IS_LOGIN, isLogin);
    f.setArguments(args);
    return f;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(getLayoutId(), container, false);
  }

  protected int getLayoutId() {
    return R.layout.fragment_login_sign_up;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    facebookRequestedPermissions = Arrays.asList("email", "user_friends");

    final Bundle args = getArguments();
    useSkipButton = args != null && args.getBoolean(SKIP_BUTTON, false);
    if (useSkipButton) {
      // FIXME: 20/2/2017 sithengineer
      Log.e(TAG, "implement this.");
    }

    isLogin = args != null && args.getBoolean(IS_LOGIN, true);

    bindViews(view);

    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    attachPresenter(new LoginSignUpPresenter(this, accountManager, facebookRequestedPermissions),
        savedInstanceState);
  }

  private void bindViews(View view) {
    forgotPasswordButton = (TextView) view.findViewById(R.id.forgot_password);

    googleLoginButton = (SignInButton) view.findViewById(R.id.g_sign_in_button);

    buttonLogin = (Button) view.findViewById(R.id.button_login);
    buttonSignUp = (Button) view.findViewById(R.id.button_sign_up);
    aptoideEmailEditText = (EditText) view.findViewById(R.id.username);
    aptoidePasswordEditText = (EditText) view.findViewById(R.id.password);
    hideShowAptoidePasswordButton = (Button) view.findViewById(R.id.btn_show_hide_pass);

    facebookLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();

    loginArea = view.findViewById(R.id.login_button_area);
    signUpArea = view.findViewById(R.id.sign_up_button_area);

    successSnackbar =
        Snackbar.make(buttonLogin, cm.aptoide.accountmanager.R.string.login_successful,
            Snackbar.LENGTH_SHORT);

    final Context context = getContext();

    facebookEmailRequiredDialog = new AlertDialog.Builder(context).setMessage(
        cm.aptoide.accountmanager.R.string.facebook_email_permission_regected_message)
        .setPositiveButton(cm.aptoide.accountmanager.R.string.facebook_grant_permission_button,
            (dialog, which) -> {
              facebookLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email"));
            })
        .setNegativeButton(android.R.string.cancel, null)
        .create();

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(context);
    showLoginOrSignUpArea();
  }

  private void showLoginOrSignUpArea() {
    loginArea.setVisibility(isLogin ? View.VISIBLE : View.GONE);
    signUpArea.setVisibility(isLogin ? View.GONE : View.VISIBLE);
  }

  @Override protected SignInButton getGoogleButton() {
    return googleLoginButton;
  }

  @Override public void showGoogleLogin() {
    super.showGoogleLogin();
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    super.hideGoogleLogin();
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public Observable<GoogleAccountViewModel> googleLoginClick() {
    return Observable.empty();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override protected void showGoogleLoginError() {
    ShowMessage.asSnack(buttonLogin, cm.aptoide.accountmanager.R.string.unknown_error);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(Throwable throwable) {
    final String message;
    if (throwable instanceof OAuthException) {
      final OAuth oAuth = ((OAuthException) throwable).getoAuth();
      message = getString(
          ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getErrors().get(0).getCode()));
    } else {
      message = getString(cm.aptoide.accountmanager.R.string.unknown_error);
    }
    ShowMessage.asSnack(buttonLogin, message);
  }

  @Override public void showFacebookLogin() {
    FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    facebookLoginButton.setReadPermissions(facebookRequestedPermissions);
    facebookLoginButton.setVisibility(View.VISIBLE);
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(),
            loginResult.getRecentlyDeniedPermissions()));
      }

      @Override public void onCancel() {
        showFacebookLoginError(cm.aptoide.accountmanager.R.string.unknown_error);
      }

      @Override public void onError(FacebookException error) {
        showFacebookLoginError(cm.aptoide.accountmanager.R.string.error_occured);
      }
    });
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  @Override public void showCheckAptoideCredentialsMessage() {
    ShowMessage.asSnack(buttonLogin, cm.aptoide.accountmanager.R.string.fields_cannot_empty);
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void navigateToForgotPasswordView() {
    startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://m.aptoide.com/account/password-recovery")));
  }

  @Override public void showPassword() {
    isPasswordVisible = false;
    aptoidePasswordEditText.setTransformationMethod(null);
  }

  @Override public void hidePassword() {
    isPasswordVisible = true;
    aptoidePasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
  }

  @Override public Observable<Void> showHidePasswordClick() {
    return RxView.clicks(hideShowAptoidePasswordButton);
  }

  @Override public Observable<Void> forgotPasswordClick() {
    return RxView.clicks(forgotPasswordButton);
  }

  @Override public void navigateToMainView() {
    Fragment home =
        HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(), StoreContext.home,
            V8Engine.getConfiguration().getDefaultTheme());

    // clean all the back stack in the Fragment Manager
    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      while (fragmentManager.popBackStackImmediate()) ;
    }

    getNavigationManager().navigateTo(home);
  }

  @Override public Observable<Void> successMessageShown() {
    return RxSnackbar.dismisses(successSnackbar).map(dismiss -> null);
  }

  @Override public void showSuccessMessage() {
    successSnackbar.show();
  }

  @Override public Observable<FacebookAccountViewModel> facebookLoginClick() {
    return facebookLoginSubject;
  }

  @Override public Observable<AptoideAccountViewModel> aptoideLoginClick() {
    return RxView.clicks(buttonLogin)
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  @Override public Observable<AptoideAccountViewModel> aptoideSignUpClick() {
    return RxView.clicks(buttonSignUp)
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  @Override public boolean isPasswordVisible() {
    return isPasswordVisible;
  }

  @Override public void showAptoideLogin() {
    isLogin = true;
    showLoginOrSignUpArea();
  }

  @Override public void showSignUp() {
    isLogin = false;
    showLoginOrSignUpArea();
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asSnack(buttonLogin, errorRes);
  }
}
