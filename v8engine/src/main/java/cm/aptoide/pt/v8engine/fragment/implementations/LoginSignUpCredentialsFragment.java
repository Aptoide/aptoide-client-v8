package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.LoginPreferences;
import cm.aptoide.pt.v8engine.activity.CreateUserActivity;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.GoogleLoginFragment;
import cm.aptoide.pt.v8engine.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.v8engine.view.AccountErrorMapper;
import cm.aptoide.pt.v8engine.view.LoginSignUpCredentialsView;
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import cm.aptoide.pt.v8engine.viewModel.AptoideAccountViewModel;
import cm.aptoide.pt.v8engine.viewModel.FacebookAccountViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

public class LoginSignUpCredentialsFragment extends GoogleLoginFragment
    implements LoginSignUpCredentialsView {

  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String CLEAN_BACK_STACK = "clean_back_stack";

  private ProgressDialog progressDialog;
  private CallbackManager callbackManager;
  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;
  private LoginManager facebookLoginManager;
  private AlertDialog facebookEmailRequiredDialog;
  private Button googleLoginButton;
  private View facebookLoginButton;
  private Button hideShowAptoidePasswordButton;
  private View loginArea;
  private View signUpArea;
  private EditText aptoideEmailEditText;
  private EditText aptoidePasswordEditText;
  private TextView forgotPasswordButton;
  private Button buttonSignUp;
  private Button buttonLogin;
  private View loginSignupSelectionArea;
  private Button loginSelectionButton;
  private Button signUpSelectionButton;
  private TextView termsAndConditions;
  private View separator;

  private boolean isPasswordVisible = false;
  private View credentialsEditTextsArea;
  private BottomSheetBehavior<View> bottomSheetBehavior;
  private ThrowableToStringMapper errorMapper;
  private LoginSignUpCredentialsPresenter presenter;
  private List<String> facebookRequestedPermissions;

  public static LoginSignUpCredentialsFragment newInstance(boolean dismissToNavigateToMainView,
      boolean cleanBackStack) {
    final LoginSignUpCredentialsFragment fragment = new LoginSignUpCredentialsFragment();

    final Bundle bundle = new Bundle();
    bundle.putBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW, dismissToNavigateToMainView);
    bundle.putBoolean(CLEAN_BACK_STACK, cleanBackStack);
    fragment.setArguments(bundle);

    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    errorMapper = new AccountErrorMapper(getContext());
    facebookRequestedPermissions = Arrays.asList("email", "user_friends");
    presenter = new LoginSignUpCredentialsPresenter(this,
        ((V8Engine) getContext().getApplicationContext()).getAccountManager(),
        facebookRequestedPermissions,
        new LoginPreferences(getContext(), V8Engine.getConfiguration(),
            GoogleApiAvailability.getInstance()),
        getArguments().getBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW),
        getArguments().getBoolean(CLEAN_BACK_STACK));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_login_sign_up_credentials, container, false);
  }

  @Override public boolean onBackPressed() {
    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    return tryCloseLoginBottomSheet() || super.onBackPressed();
  }

  @Override public Observable<Void> showAptoideLoginAreaClick() {
    return RxView.clicks(loginSelectionButton);
  }

  @Override public Observable<Void> showAptoideSignUpAreaClick() {
    return RxView.clicks(signUpSelectionButton);
  }

  @Override public void showAptoideSignUpArea() {
    setAptoideSignUpLoginAreaVisible();
    loginArea.setVisibility(View.GONE);
    signUpArea.setVisibility(View.VISIBLE);
    separator.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
  }

  private void setAptoideSignUpLoginAreaVisible() {
    credentialsEditTextsArea.setVisibility(View.VISIBLE);
    loginSignupSelectionArea.setVisibility(View.GONE);
    if (bottomSheetBehavior != null) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }

  @Override public void showAptoideLoginArea() {
    setAptoideSignUpLoginAreaVisible();
    loginArea.setVisibility(View.VISIBLE);
    signUpArea.setVisibility(View.GONE);
    separator.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(Throwable throwable) {
    // FIXME: 23/2/2017 sithengineer find a better solution than this.
    ShowMessage.asToast(getContext(), errorMapper.map(throwable));
  }

  @Override public void showFacebookLogin() {
    FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    facebookLoginButton.setVisibility(View.VISIBLE);
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(),
            loginResult.getRecentlyDeniedPermissions()));
      }

      @Override public void onCancel() {
        showFacebookLoginError(R.string.facebook_login_cancelled);
      }

      @Override public void onError(FacebookException error) {
        showFacebookLoginError(cm.aptoide.accountmanager.R.string.error_occured);
      }
    });
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public void navigateToForgotPasswordView() {
    // FIXME: remove hardcoded links
    Uri mobilePageUri = Uri.parse("http://m.aptoide.com/account/password-recovery");
    startActivity(new Intent(Intent.ACTION_VIEW, mobilePageUri));
  }

  @Override public void showPassword() {
    isPasswordVisible = true;
    aptoidePasswordEditText.setTransformationMethod(null);
    hideShowAptoidePasswordButton.setBackgroundResource(R.drawable.icon_open_eye);
  }

  @Override public void hidePassword() {
    isPasswordVisible = false;
    aptoidePasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    hideShowAptoidePasswordButton.setBackgroundResource(R.drawable.icon_closed_eye);
  }

  @Override public Observable<Void> showHidePasswordClick() {
    return RxView.clicks(hideShowAptoidePasswordButton);
  }

  @Override public Observable<Void> forgotPasswordClick() {
    return RxView.clicks(forgotPasswordButton);
  }

  @Override public void navigateToMainView() {
    final NavigationManagerV4 navManager = getNavigationManager();
    Fragment home =
        HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(), StoreContext.home,
            V8Engine.getConfiguration().getDefaultTheme());
    navManager.cleanBackStack();
    navManager.navigateTo(home);
  }

  @Override public void goBack() {
    // close login / signup bottom sheet
    onBackPressed();
    // pop this fragment from stack
    getActivity().onBackPressed();
  }

  private boolean tryCloseLoginBottomSheet() {
    if (credentialsEditTextsArea.getVisibility() == View.VISIBLE) {
      credentialsEditTextsArea.setVisibility(View.GONE);
      loginSignupSelectionArea.setVisibility(View.VISIBLE);
      loginArea.setVisibility(View.GONE);
      signUpArea.setVisibility(View.GONE);
      separator.setVisibility(View.VISIBLE);
      termsAndConditions.setVisibility(View.VISIBLE);
      return true;
    }
    return false;
  }

  @Override public void dismiss() {
    getActivity().finish();
  }

  @Override public void hideKeyboard() {
    AptoideUtils.SystemU.hideKeyboard(getActivity());
  }

  @Override public Observable<FacebookAccountViewModel> facebookLoginClick() {
    return facebookLoginSubject.doOnNext(
        __ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.CONNECT_FACEBOOK));
  }

  @Override public Observable<AptoideAccountViewModel> aptoideLoginClick() {
    return RxView.clicks(buttonLogin)
        .doOnNext(__ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.LOGIN))
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  @Override public Observable<AptoideAccountViewModel> aptoideSignUpClick() {
    return RxView.clicks(buttonSignUp)
        .doOnNext(__ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.JOIN_APTOIDE))
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  @Override public boolean isPasswordVisible() {
    return isPasswordVisible;
  }

  @Override public void navigateToCreateProfile() {
    Intent i = new Intent(getContext(), CreateUserActivity.class);
    FragmentActivity parent = getActivity();
    parent.startActivity(i);
    getNavigationManager().cleanBackStack();
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asToast(getContext(), errorRes);
  }

  @Override public void googleLoginClicked() {
    Analytics.Account.clickIn(Analytics.Account.StartupClick.CONNECT_GOOGLE);
  }

  @Override protected Button getGoogleButton() {
    return googleLoginButton;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    bindViews(view);

    attachPresenter(presenter, savedInstanceState);
  }

  private void bindViews(View view) {
    forgotPasswordButton = (TextView) view.findViewById(R.id.forgot_password);

    googleLoginButton = (Button) view.findViewById(R.id.google_login_button);

    buttonLogin = (Button) view.findViewById(R.id.button_login);
    buttonSignUp = (Button) view.findViewById(R.id.button_sign_up);
    buttonSignUp.setText(String.format(getString(R.string.join_company), getCompanyName()));

    aptoideEmailEditText = (EditText) view.findViewById(R.id.username);
    aptoidePasswordEditText = (EditText) view.findViewById(R.id.password);
    hideShowAptoidePasswordButton = (Button) view.findViewById(R.id.btn_show_hide_pass);

    facebookLoginButton = view.findViewById(R.id.fb_login_button);
    //facebookLoginButton.setFragment(this);
    //facebookLoginButton.setReadPermissions(facebookRequestedPermissions);
    RxView.clicks(facebookLoginButton)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(
            __ -> facebookLoginManager.logInWithReadPermissions(LoginSignUpCredentialsFragment.this,
                facebookRequestedPermissions));

    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();

    loginSignupSelectionArea = view.findViewById(R.id.login_signup_selection_layout);
    credentialsEditTextsArea = view.findViewById(R.id.credentials_edit_texts);
    signUpSelectionButton = (Button) view.findViewById(R.id.show_join_aptoide_area);
    loginSelectionButton = (Button) view.findViewById(R.id.show_login_with_aptoide_area);
    signUpSelectionButton.setText(
        String.format(getString(R.string.join_company), getCompanyName()));
    loginArea = view.findViewById(R.id.login_button_area);
    signUpArea = view.findViewById(R.id.sign_up_button_area);
    termsAndConditions = (TextView) view.findViewById(R.id.terms_and_conditions);
    separator = (View) view.findViewById(R.id.separator);

    final Context context = getContext();

    facebookEmailRequiredDialog = new AlertDialog.Builder(context).setMessage(
        cm.aptoide.accountmanager.R.string.facebook_email_permission_regected_message)
        .setPositiveButton(cm.aptoide.accountmanager.R.string.facebook_grant_permission_button,
            (dialog, which) -> {
              facebookLoginManager.logInWithReadPermissions(this, Arrays.asList("email"));
            })
        .setNegativeButton(android.R.string.cancel, null)
        .create();

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(context);

    bottomSheetBehavior =
        BottomSheetBehavior.from(view.getRootView().findViewById(R.id.login_signup_layout));
  }

  public String getCompanyName() {
    return ((V8Engine) getActivity().getApplication()).createConfiguration().getMarketName();
  }

  @Override protected void showGoogleLoginError() {
    ShowMessage.asToast(getContext(), R.string.google_login_cancelled);
  }
}
