package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.OAuthException;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.fragment.GoogleLoginFragment;
import cm.aptoide.pt.v8engine.presenter.LoginPresenter;
import cm.aptoide.pt.v8engine.view.LoginView;
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

public class LoginSignUpFragment extends GoogleLoginFragment implements LoginView {

  private ProgressDialog progressDialog;
  private CallbackManager callbackManager;

  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;

  private LoginManager facebookLoginManager;
  private AlertDialog facebookEmailRequiredDialog;
  private List<String> facebookRequestedPermissions;
  private Snackbar successSnackbar;

  private SignInButton googleLoginButton;
  private LoginButton facebookLoginButton;
  private Button showLoginButton;
  private Button hideShowAptoidePasswordButton;
  private View inputCredentials;
  private View loginArea;
  private View signUpArea;
  private Button showSignUpButton;
  private EditText aptoideEmailEditText;
  private EditText aptoidePasswordEditText;
  private TextView forgotPasswordButton;
  private Button buttonSignUp;
  private Button buttonLogin;

  public static LoginSignUpFragment newInstance() {
    return new LoginSignUpFragment();
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

    bindViews(view);

    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    attachPresenter(new LoginPresenter(this, accountManager, facebookRequestedPermissions),
        savedInstanceState);
  }

  private void bindViews(View view) {
    forgotPasswordButton = (TextView) view.findViewById(R.id.forgot_password);

    googleLoginButton = (SignInButton) view.findViewById(R.id.g_sign_in_button);

    showLoginButton = (Button) view.findViewById(R.id.button_select_login);
    buttonLogin = (Button) view.findViewById(R.id.button_login);
    showSignUpButton = (Button) view.findViewById(R.id.button_select_sign_up);
    buttonSignUp = (Button) view.findViewById(R.id.button_sign_up);
    aptoideEmailEditText = (EditText) view.findViewById(R.id.username);
    aptoidePasswordEditText = (EditText) view.findViewById(R.id.password);
    hideShowAptoidePasswordButton = (Button) view.findViewById(R.id.btn_show_hide_pass);

    facebookLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();

    inputCredentials = view.findViewById(R.id.login_fields);
    loginArea = view.findViewById(R.id.login_button_area);
    signUpArea = view.findViewById(R.id.sign_up_button_area);

    successSnackbar =
        Snackbar.make(showLoginButton, cm.aptoide.accountmanager.R.string.login_successful,
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
  }

  @Override protected SignInButton getGoogleButton() {
    return googleLoginButton;
  }

  @Override protected void showGoogleLoginError() {
    ShowMessage.asSnack(showLoginButton, cm.aptoide.accountmanager.R.string.unknown_error);
  }

  @Override public void showGoogleLogin() {
    super.showGoogleLogin();
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    super.hideGoogleLogin();
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public Observable<GoogleAccountViewModel> googleLoginClick() {
    return Observable.empty();
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
    ShowMessage.asSnack(showLoginButton, message);
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
    ShowMessage.asSnack(showLoginButton, cm.aptoide.accountmanager.R.string.fields_cannot_empty);
  }

  @Override public void setLoginAreaVisible() {
    toggleInputFieldsVisibility(true);
    showLoginButton.setVisibility(View.GONE);
    loginArea.setVisibility(View.VISIBLE);
    signUpArea.setVisibility(View.GONE);
  }

  private void toggleInputFieldsVisibility(boolean visible) {
    inputCredentials.setVisibility(visible ? View.VISIBLE : View.GONE);
    if(!visible) {
      showSignUpButton.setVisibility(View.VISIBLE);
      showLoginButton.setVisibility(View.VISIBLE);
    }
  }

  @Override public void setSignUpAreaVisible() {
    toggleInputFieldsVisibility(true);
    showSignUpButton.setVisibility(View.GONE);
    loginArea.setVisibility(View.GONE);
    signUpArea.setVisibility(View.VISIBLE);
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void navigateToForgotPasswordView() {
    startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://m.aptoide.com/account/password-recovery")));
  }

  @Override public void showPassword() {
    // todo
    Logger.w(this.getClass().getName(), "to do");
  }

  @Override public void hidePassword() {
    // todo
    Logger.w(this.getClass().getName(), "to do");
  }

  @Override public Observable<Void> showHidePasswordClick() {
    return RxView.clicks(hideShowAptoidePasswordButton);
  }

  @Override public Observable<Void> forgotPasswordClick() {
    return RxView.clicks(forgotPasswordButton);
  }

  @Override public Observable<Void> showSignUpClick() {
    return RxView.clicks(showSignUpButton);
  }

  @Override public void navigateToMainView() {
    getActivity().finish();
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

  @Override public Observable<Void> showAptoideLoginClick() {
    return RxView.clicks(showLoginButton);
  }

  @Override public Observable<AptoideAccountViewModel> aptoideLoginClick() {
    return RxView.clicks(buttonLogin)
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asSnack(showLoginButton, errorRes);
  }

  @Override public boolean onBackPressed() {
    if (areInputFieldsVisibile()) {
      toggleInputFieldsVisibility(false);
      return true;
    }

    return super.onBackPressed();
  }

  // to use when back is pressed
  private boolean areInputFieldsVisibile() {
    return inputCredentials.getVisibility() == View.VISIBLE;
  }
}
