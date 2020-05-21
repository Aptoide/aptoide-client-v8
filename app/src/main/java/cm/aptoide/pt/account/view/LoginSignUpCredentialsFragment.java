package cm.aptoide.pt.account.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.presenter.LoginSignupCredentialsFlavorPresenter;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LoginSignUpCredentialsFragment extends GooglePlayServicesFragment
    implements LoginSignUpCredentialsView, NotBottomNavigationView {

  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String CLEAN_BACK_STACK = "clean_back_stack";

  private static final String USERNAME_KEY = "username_key";
  private static final String CODE_KEY = "code_key";
  @Inject LoginSignupCredentialsFlavorPresenter presenter;
  @Inject ScreenOrientationManager orientationManager;
  @Inject AccountAnalytics accountAnalytics;
  @Inject @Named("marketName") String marketName;
  @Inject ThemeManager themeManager;
  private ProgressDialog progressDialog;
  private RxAlertDialog facebookEmailRequiredDialog;
  private Button googleLoginButton;
  private View facebookLoginButton;
  private View loginArea;
  private TextView codeInputError;
  private TextView resendCode;
  private TextView emailInputError;
  private TextView loginDescription;
  private EditText aptoideEmailEditText;
  private TextView aptoideEmailLocked;
  private EditText codeInputEditText;
  private Button buttonLogin;
  private Button finishButton;
  private View loginSignupSelectionArea;
  private Button loginSelectionButton;
  private TextView termsAndConditions;
  private View credentialsEditTextsArea;
  private View codeInputArea;
  private View socialLoginArea;
  private BottomSheetBehavior<View> bottomSheetBehavior;
  private View rootView;
  private CheckBox termsConditionCheckBox;
  private Drawable checkboxDrawable;

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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rootView = getActivity().findViewById(android.R.id.content);

    googleLoginButton = (Button) view.findViewById(R.id.google_login_button);

    buttonLogin = (Button) view.findViewById(R.id.button_login);

    finishButton = (Button) view.findViewById(R.id.button_finish);

    aptoideEmailEditText = (EditText) view.findViewById(R.id.username);
    aptoideEmailLocked = view.findViewById(R.id.email_set);

    codeInputEditText = (EditText) view.findViewById(R.id.code_input);

    facebookLoginButton = view.findViewById(R.id.fb_login_button);

    loginSignupSelectionArea = view.findViewById(R.id.login_signup_selection_layout);
    credentialsEditTextsArea = view.findViewById(R.id.credentials_edit_texts);
    codeInputArea = view.findViewById(R.id.code_input_area);
    socialLoginArea = view.findViewById(R.id.social_login_area);
    loginSelectionButton = (Button) view.findViewById(R.id.show_login_with_aptoide_area);

    loginArea = view.findViewById(R.id.login_button_area);
    codeInputError = view.findViewById(R.id.submit_code_error);
    resendCode = view.findViewById(R.id.resubmit_code);

    emailInputError = view.findViewById(R.id.email_error_message);
    loginDescription = view.findViewById(R.id.use_case_description);

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext(), themeManager).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    termsConditionCheckBox = (CheckBox) view.findViewById(R.id.tc_checkbox);
    termsAndConditions = (TextView) view.findViewById(R.id.terms_and_conditions);

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);

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
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    unregisterClickHandler(presenter);
    unlockScreenRotation();
    termsAndConditions = null;
    credentialsEditTextsArea = null;
    termsConditionCheckBox = null;
    super.onDestroyView();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
      codeInputEditText.setText(savedInstanceState.getString(CODE_KEY, ""));
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (outState != null && aptoideEmailEditText != null) {
      outState.putString(USERNAME_KEY, aptoideEmailEditText.getText()
          .toString());
      outState.putString(CODE_KEY, codeInputEditText.getText()
          .toString());
    }
  }

  @Override public Observable<Boolean> showAptoideLoginAreaClick() {
    return RxView.clicks(loginSelectionButton)
        .map(event -> termsConditionCheckBox.isChecked());
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

  @Override public Observable<String> emailSubmitEvent() {
    return RxView.clicks(buttonLogin)
        .doOnNext(__ -> accountAnalytics.clickIn(AccountAnalytics.StartupClick.LOGIN,
            getStartupClickOrigin()))
        .map(click -> aptoideEmailEditText.getText()
            .toString());
  }

  @Override public Observable<Void> termsAndConditionsClickEvent() {
    return termsAndConditionsSubject;
  }

  @Override public Observable<Void> privacyPolicyClickEvent() {
    return privacyPolicySubject;
  }

  @Override public void showAptoideLoginArea() {
    setAptoideLoginAreaVisible();
    hideSocialLoginArea();
    loginArea.setVisibility(View.VISIBLE);
    termsConditionCheckBox.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
    codeInputError.setVisibility(View.GONE);
    resendCode.setVisibility(View.VISIBLE);
    loginDescription.setVisibility(View.VISIBLE);
    emailInputError.setVisibility(View.GONE);
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
    //Shifts the bottomsheet up and then down again to create space for the error snack when in portrait
    Snackbar snackbar = Snackbar.make(rootView, getString(R.string.signup_message_no_tandc_error),
        Snackbar.LENGTH_SHORT);

    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      snackbar.addCallback(new Snackbar.Callback() {

        @Override public void onShown(Snackbar snackbar) {
          float newHeight = 360 * getResources().getDisplayMetrics().density;
          bottomSheetBehavior.setPeekHeight((int) newHeight);
        }
      });
    }
    snackbar.show();

    Drawable.ConstantState constantState = checkboxDrawable.getConstantState();

    if (constantState != null) {
      Drawable replacementDrawable = constantState.newDrawable()
          .mutate();
      replacementDrawable.setColorFilter(getResources().getColor(R.color.red),
          PorterDuff.Mode.SRC_ATOP);
      termsConditionCheckBox.setButtonDrawable(replacementDrawable);
    }

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
    if (credentialsEditTextsArea.getVisibility() == View.VISIBLE) {
      credentialsEditTextsArea.setVisibility(View.GONE);
      loginSignupSelectionArea.setVisibility(View.VISIBLE);
      codeInputArea.setVisibility(View.GONE);
      loginArea.setVisibility(View.GONE);
      socialLoginArea.setVisibility(View.VISIBLE);
      if (shouldShowTCandPP) {
        termsConditionCheckBox.setVisibility(View.VISIBLE);
        termsAndConditions.setVisibility(View.VISIBLE);
      }
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

  public void hideTCandPP() {
    termsConditionCheckBox.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
  }

  public void showTCandPP() {
    checkboxDrawable = CompoundButtonCompat.getButtonDrawable(termsConditionCheckBox);
    termsConditionCheckBox.setVisibility(View.VISIBLE);

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

    termsAndConditions.setText(privacyAndTermsSpan);
    termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());
    termsAndConditions.setVisibility(View.VISIBLE);
  }

  @Override public Observable<AptoideCredentials> aptoideLoginEvent() {
    return RxView.clicks(finishButton)
        .map(v -> new AptoideCredentials(aptoideEmailLocked.getText()
            .toString(), codeInputEditText.getText()
            .toString(), termsConditionCheckBox.isChecked()));
  }

  @Override public void showAptoideLoginCodeArea(String email) {
    credentialsEditTextsArea.setVisibility(View.GONE);
    loginSignupSelectionArea.setVisibility(View.GONE);
    aptoideEmailLocked.setText(email);
    codeInputArea.setVisibility(View.VISIBLE);
    if (bottomSheetBehavior != null) {
      float newHeight = 320 * getResources().getDisplayMetrics().density;
      bottomSheetBehavior.setPeekHeight((int) newHeight);
    }
  }

  @Override public Observable<Void> emailSetClickEvent() {
    return RxView.clicks(aptoideEmailLocked);
  }

  @Override public void showLoginError(String message) {
    codeInputError.setText(message);
    codeInputError.setVisibility(View.VISIBLE);
    resendCode.setVisibility(View.GONE);
  }

  @Override public void showEmailError(String message) {
    emailInputError.setText(message);
    emailInputError.setVisibility(View.VISIBLE);
    loginDescription.setVisibility(View.GONE);
  }

  private void hideSocialLoginArea() {
    socialLoginArea.setVisibility(View.GONE);
  }

  private AccountAnalytics.StartupClickOrigin getStartupClickOrigin() {
    if (loginArea.getVisibility() == View.VISIBLE) {
      return AccountAnalytics.StartupClickOrigin.LOGIN_UP;
    } else {
      return AccountAnalytics.StartupClickOrigin.MAIN;
    }
  }

  private void setAptoideLoginAreaVisible() {
    codeInputArea.setVisibility(View.GONE);
    credentialsEditTextsArea.setVisibility(View.VISIBLE);
    loginSignupSelectionArea.setVisibility(View.GONE);
    if (bottomSheetBehavior != null) {
      float newHeight = 320 * getResources().getDisplayMetrics().density;
      bottomSheetBehavior.setPeekHeight((int) newHeight);
    }
  }
}
