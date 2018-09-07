package cm.aptoide.pt.account.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LoginSignUpCredentialsFragment extends GooglePlayServicesFragment
    implements LoginSignUpCredentialsView, NotBottomNavigationView {

  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String CLEAN_BACK_STACK = "clean_back_stack";

  private static final String USERNAME_KEY = "username_key";
  private static final String PASSWORD_KEY = "password_key";
  @Inject LoginSignUpCredentialsPresenter presenter;
  @Inject ScreenOrientationManager orientationManager;
  @Inject AccountAnalytics accountAnalytics;
  private ProgressDialog progressDialog;
  private RxAlertDialog facebookEmailRequiredDialog;
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
  private View rootView;
  private CheckBox termsConditionCheckBox;
  private Drawable checkboxDrawable;

  private String marketName;
  private PublishSubject<Void> privacyPolicySubject;
  private PublishSubject<Void> termsAndConditionsSubject;

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
    getFragmentComponent(savedInstanceState).inject(this);
    marketName = ((AptoideApplication) getActivity().getApplication()).getMarketName();
    privacyPolicySubject = PublishSubject.create();
    termsAndConditionsSubject = PublishSubject.create();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroy() {
    privacyPolicySubject = null;
    termsAndConditionsSubject = null;
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

    if (savedInstanceState != null) {
      aptoideEmailEditText.setText(savedInstanceState.getString(USERNAME_KEY, ""));
      aptoidePasswordEditText.setText(savedInstanceState.getString(PASSWORD_KEY, ""));
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (outState != null && aptoideEmailEditText != null && aptoidePasswordEditText != null) {
      outState.putString(USERNAME_KEY, aptoideEmailEditText.getText()
          .toString());
      outState.putString(PASSWORD_KEY, aptoidePasswordEditText.getText()
          .toString());
    }
  }

  @Override public Observable<Void> showAptoideLoginAreaClick() {
    return RxView.clicks(loginSelectionButton);
  }

  @Override public Observable<Boolean> showAptoideSignUpAreaClick() {
    return RxView.clicks(signUpSelectionButton)
        .map(event -> termsConditionCheckBox.isChecked());
  }

  @Override public Observable<Boolean> googleSignUpEvent() {
    return RxView.clicks(googleLoginButton)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.CONNECT_GOOGLE,
            getStartupClickOrigin()))
        .map(event -> termsConditionCheckBox.isChecked());
  }

  @Override public Observable<Void> showHidePasswordClick() {
    return RxView.clicks(hideShowAptoidePasswordButton);
  }

  @Override public Observable<Void> forgotPasswordClick() {
    return RxView.clicks(forgotPasswordButton);
  }

  @Override public Observable<Void> facebookSignUpWithRequiredPermissionsInEvent() {
    return facebookEmailRequiredDialog.positiveClicks()
        .map(dialog -> null);
  }

  @Override public Observable<Boolean> facebookSignUpEvent() {
    return RxView.clicks(facebookLoginButton)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.CONNECT_FACEBOOK,
            getStartupClickOrigin()))
        .map(event -> termsConditionCheckBox.isChecked());
  }

  @Override public Observable<AptoideCredentials> aptoideLoginEvent() {
    return RxView.clicks(buttonLogin)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.LOGIN,
            getStartupClickOrigin()))
        .map(click -> getCredentials());
  }

  @Override public Observable<AptoideCredentials> aptoideSignUpEvent() {
    return RxView.clicks(buttonSignUp)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.JOIN_APTOIDE,
            getStartupClickOrigin()))
        .map(click -> getCredentials());
  }

  @Override public Observable<Void> termsAndConditionsClickEvent() {
    return termsAndConditionsSubject;
  }

  @Override public Observable<Void> privacyPolicyClickEvent() {
    return privacyPolicySubject;
  }

  @Override public void showAptoideSignUpArea() {
    setAptoideSignUpLoginAreaVisible();
    loginArea.setVisibility(View.GONE);
    signUpArea.setVisibility(View.VISIBLE);
    separator.setVisibility(View.GONE);
    termsConditionCheckBox.setVisibility(View.VISIBLE);
    termsAndConditions.setVisibility(View.VISIBLE);
  }

  @Override public void showAptoideLoginArea() {
    setAptoideSignUpLoginAreaVisible();
    loginArea.setVisibility(View.VISIBLE);
    signUpArea.setVisibility(View.GONE);
    separator.setVisibility(View.GONE);
    termsConditionCheckBox.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
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

  @Override public void showTermsConditionError() {
    Snackbar.make(rootView, getString(R.string.signup_message_no_tandc_error),
        Snackbar.LENGTH_SHORT)
        .show();
    Drawable replacementDrawable = checkboxDrawable.getConstantState()
        .newDrawable()
        .mutate();
    replacementDrawable.setColorFilter(getResources().getColor(R.color.red),
        PorterDuff.Mode.SRC_ATOP);
    termsConditionCheckBox.setButtonDrawable(replacementDrawable);
    termsConditionCheckBox.setOnCheckedChangeListener(
        (buttonView, isChecked) -> termsConditionCheckBox.setButtonDrawable(checkboxDrawable));
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

  @Override public void showPassword() {
    isPasswordVisible = true;
    aptoidePasswordEditText.setTransformationMethod(null);
    hideShowAptoidePasswordButton.setBackgroundResource(R.drawable.ic_open_eye);
  }

  @Override public void hidePassword() {
    isPasswordVisible = false;
    aptoidePasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    hideShowAptoidePasswordButton.setBackgroundResource(R.drawable.ic_closed_eye);
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

  @Override public boolean tryCloseLoginBottomSheet() {
    if (credentialsEditTextsArea.getVisibility() == View.VISIBLE) {
      credentialsEditTextsArea.setVisibility(View.GONE);
      loginSignupSelectionArea.setVisibility(View.VISIBLE);
      loginArea.setVisibility(View.GONE);
      signUpArea.setVisibility(View.GONE);
      separator.setVisibility(View.VISIBLE);
      termsConditionCheckBox.setVisibility(View.VISIBLE);
      termsAndConditions.setVisibility(View.VISIBLE);
      return true;
    }
    return false;
  }

  @Override public boolean isPasswordVisible() {
    return isPasswordVisible;
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

  private AptoideCredentials getCredentials() {
    return new AptoideCredentials(aptoideEmailEditText.getText()
        .toString(), aptoidePasswordEditText.getText()
        .toString(), termsConditionCheckBox.isChecked());
  }

  private AccountAnalytics.StartupClickOrigin getStartupClickOrigin() {
    if (loginArea.getVisibility() == View.VISIBLE) {
      return AccountAnalytics.StartupClickOrigin.LOGIN_UP;
    } else if (signUpArea.getVisibility() == View.VISIBLE) {
      return AccountAnalytics.StartupClickOrigin.JOIN_UP;
    } else {
      return AccountAnalytics.StartupClickOrigin.MAIN;
    }
  }

  private void setAptoideSignUpLoginAreaVisible() {
    credentialsEditTextsArea.setVisibility(View.VISIBLE);
    loginSignupSelectionArea.setVisibility(View.GONE);
    if (bottomSheetBehavior != null) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rootView = getActivity().findViewById(android.R.id.content);
    forgotPasswordButton = (TextView) view.findViewById(R.id.forgot_password);

    googleLoginButton = (Button) view.findViewById(R.id.google_login_button);

    buttonLogin = (Button) view.findViewById(R.id.button_login);
    buttonSignUp = (Button) view.findViewById(R.id.button_sign_up);

    if ("vanilla".equalsIgnoreCase(BuildConfig.FLAVOR_product)) {
      buttonSignUp.setText(String.format(getString(R.string.onboarding_button_join_us)));
    } else {
      buttonSignUp.setText(String.format(getString(R.string.join_company), marketName));
    }
    aptoideEmailEditText = (EditText) view.findViewById(R.id.username);
    aptoidePasswordEditText = (EditText) view.findViewById(R.id.password);
    hideShowAptoidePasswordButton = (Button) view.findViewById(R.id.btn_show_hide_pass);

    facebookLoginButton = view.findViewById(R.id.fb_login_button);

    loginSignupSelectionArea = view.findViewById(R.id.login_signup_selection_layout);
    credentialsEditTextsArea = view.findViewById(R.id.credentials_edit_texts);
    signUpSelectionButton = (Button) view.findViewById(R.id.show_join_aptoide_area);
    loginSelectionButton = (Button) view.findViewById(R.id.show_login_with_aptoide_area);
    termsConditionCheckBox = (CheckBox) view.findViewById(R.id.tc_checkbox);
    checkboxDrawable = CompoundButtonCompat.getButtonDrawable(termsConditionCheckBox);

    if ("vanilla".equalsIgnoreCase(BuildConfig.FLAVOR_product)) {
      buttonSignUp.setText(String.format(getString(R.string.onboarding_button_join_us)));
    } else {
      signUpSelectionButton.setText(String.format(getString(R.string.join_company), marketName));
    }
    loginArea = view.findViewById(R.id.login_button_area);
    signUpArea = view.findViewById(R.id.sign_up_button_area);
    separator = view.findViewById(R.id.separator);

    ClickableSpan termsAndConditionsClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (termsAndConditionsSubject != null) {
          termsAndConditionsSubject.onNext(null);
        }
      }
    };

    ClickableSpan privacyPolicyClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (privacyPolicySubject != null) {
          privacyPolicySubject.onNext(null);
        }
      }
    };

    String baseString = getString(R.string.terms_and_conditions_privacy_sign_up_message);
    String termsAndConditionsPlaceHolder = getString(R.string.settings_terms_conditions);
    String privacyPolicyPlaceHolder = getString(R.string.settings_privacy_policy);
    String privacyAndTerms =
        String.format(baseString, termsAndConditionsPlaceHolder, privacyPolicyPlaceHolder);

    SpannableString privacyAndTermsSpan = new SpannableString(privacyAndTerms);
    privacyAndTermsSpan.setSpan(termsAndConditionsClickListener,
        privacyAndTerms.indexOf(termsAndConditionsPlaceHolder),
        privacyAndTerms.indexOf(termsAndConditionsPlaceHolder)
            + termsAndConditionsPlaceHolder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    privacyAndTermsSpan.setSpan(privacyPolicyClickListener,
        privacyAndTerms.indexOf(privacyPolicyPlaceHolder),
        privacyAndTerms.indexOf(privacyPolicyPlaceHolder) + privacyPolicyPlaceHolder.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    termsAndConditions = (TextView) view.findViewById(R.id.terms_and_conditions);
    termsAndConditions.setText(privacyAndTermsSpan);
    termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());

    try {
      bottomSheetBehavior = BottomSheetBehavior.from(view.getRootView()
          .findViewById(R.id.login_signup_layout));
    } catch (IllegalArgumentException ex) {
      // this happens because in landscape the R.id.login_signup_layout is not
      // a child of CoordinatorLayout
    }

    attachPresenter(presenter);
    registerClickHandler(presenter);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(presenter);
    unlockScreenRotation();
    termsAndConditions = null;
    credentialsEditTextsArea = null;
    termsConditionCheckBox = null;
    super.onDestroyView();
  }
}
