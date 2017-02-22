package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import cm.aptoide.pt.v8engine.presenter.JoinCommunityPresenter;
import cm.aptoide.pt.v8engine.view.JoinCommunityView;
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

/**
 * This fragment has too much code equal to {@link LoginSignUpFragment} due to Google / Facebook
 * login functionality. Further code refactoring is needed to migrate external source login into
 * their own fragment and include the fragment inside the necessary login / sign up views.
 */
public class JoinCommunityFragment extends GoogleLoginFragment implements JoinCommunityView {

  private Button showLoginButton;
  private Button showSignUpButton;

  private ProgressDialog progressDialog;
  private CallbackManager callbackManager;

  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;

  private LoginManager facebookLoginManager;
  private AlertDialog facebookEmailRequiredDialog;
  private List<String> facebookRequestedPermissions;
  private Snackbar successSnackbar;

  private SignInButton googleLoginButton;
  private LoginButton facebookLoginButton;
  private BottomSheetBehavior<View> bottomSheetBehavior;

  private LoginSignUpFragment fragment;

  public static Fragment newInstance() {
    return new JoinCommunityFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);
    fragment = LoginSignUpFragment.newInstance();
    // nested fragments only work using dynamic fragment addition.
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.login_signup_layout, fragment)
        .commit();

    return view;
  }

  protected int getLayoutId() {
    return R.layout.fragment_join_the_community;
  }

  @Override public boolean onBackPressed() {
    if (isBottomSheetVisible()) {
      hideBottomSheet();
      return true;
    }

    return super.onBackPressed();
  }

  private boolean isBottomSheetVisible() {
    return bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN;
  }

  private void hideBottomSheet() {
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    facebookRequestedPermissions = Arrays.asList("email", "user_friends");

    bindViews(view);

    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();

    attachPresenter(new JoinCommunityPresenter(this, accountManager, facebookRequestedPermissions),
        savedInstanceState);
  }

  private void bindViews(View view) {
    showLoginButton = (Button) view.findViewById(R.id.button_select_login);
    showSignUpButton = (Button) view.findViewById(R.id.button_select_sign_up);

    googleLoginButton = (SignInButton) view.findViewById(R.id.g_sign_in_button);

    facebookLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();

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

    bottomSheetBehavior =
        BottomSheetBehavior.from(view.getRootView().findViewWithTag("bottom_sheet"));
    bottomSheetBehavior.setHideable(true);
    bottomSheetBehavior.setSkipCollapsed(true);
    hideBottomSheet();
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

  @Override public Observable<Void> showAptoideLoginClick() {
    return RxView.clicks(showLoginButton);
  }

  @Override public Observable<Void> showSignUpClick() {
    return RxView.clicks(showSignUpButton);
  }

  @Override public Observable<Void> successMessageShown() {
    return RxSnackbar.dismisses(successSnackbar).map(dismiss -> null);
  }

  @Override public Observable<FacebookAccountViewModel> facebookLoginClick() {
    return facebookLoginSubject;
  }

  @Override public void showSuccessMessage() {
    successSnackbar.show();
  }

  @Override public void setLoginAreaVisible() {
    if (fragment != null) {
      showBottomSheet();
      fragment.showAptoideLogin();
    }
  }

  @Override public void setSignUpAreaVisible() {
    if (fragment != null) {
      showBottomSheet();
      fragment.showSignUp();
    }
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
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

  private void showBottomSheet() {
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asSnack(showLoginButton, errorRes);
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

  @Override protected void showGoogleLoginError() {
    ShowMessage.asSnack(showLoginButton, cm.aptoide.accountmanager.R.string.unknown_error);
  }
}
