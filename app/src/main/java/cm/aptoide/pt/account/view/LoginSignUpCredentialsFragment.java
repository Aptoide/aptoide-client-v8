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
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LoginSignUpCredentialsFragment extends GooglePlayServicesFragment
    implements LoginSignUpCredentialsView, MagicLinkView, NotBottomNavigationView {

  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String CLEAN_BACK_STACK = "clean_back_stack";

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
  private TextView loginDescription;
  private View loginSignupSelectionArea;
  private Button connectWithEmailButton;
  private TextView termsAndConditions;
  private SendMagicLinkView sendMagicLinkView;
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

    googleLoginButton = view.findViewById(R.id.google_login_button);

    facebookLoginButton = view.findViewById(R.id.fb_login_button);

    loginSignupSelectionArea = view.findViewById(R.id.login_signup_selection_layout);
    sendMagicLinkView = view.findViewById(R.id.send_magic_link_view);
    socialLoginArea = view.findViewById(R.id.social_login_area);
    connectWithEmailButton = view.findViewById(R.id.show_login_with_aptoide_area);

    loginDescription = view.findViewById(R.id.use_case_description);

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext(), themeManager).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    termsConditionCheckBox = view.findViewById(R.id.tc_checkbox);
    termsAndConditions = view.findViewById(R.id.terms_and_conditions);

    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);

    try {
      bottomSheetBehavior = BottomSheetBehavior.from(view.getRootView()
          .findViewById(R.id.login_signup_layout));
    } catch (IllegalArgumentException ex) {
      // this happens because in landscape the R.id.login_signup_layout is not
      // a child of CoordinatorLayout
    }

    attachPresenter(new CompositePresenter(Arrays.asList(presenter, sendMagicLinkPresenter)));
    registerClickHandler(presenter);
  }

  @Override public void onDestroyView() {
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    unregisterClickHandler(presenter);
    unlockScreenRotation();
    termsAndConditions = null;
    sendMagicLinkView = null;
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

    //if (savedInstanceState != null) {
    //aptoideEmailEditText.setText(savedInstanceState.getString(USERNAME_KEY, ""));
    //}
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    //if (outState != null && aptoideEmailEditText != null) {
    //  outState.putString(USERNAME_KEY, aptoideEmailEditText.getText()
    //      .toString());
    //}
  }

  @Override public Observable<Boolean> showAptoideLoginAreaClick() {
    return RxView.clicks(connectWithEmailButton)
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

  @Override public Observable<Void> termsAndConditionsClickEvent() {
    return termsAndConditionsSubject;
  }

  @Override public Observable<Void> privacyPolicyClickEvent() {
    return privacyPolicySubject;
  }

  @Override public void showAptoideLoginArea() {
    setAptoideLoginAreaVisible();
    hideSocialLoginArea();
    termsConditionCheckBox.setVisibility(View.GONE);
    termsAndConditions.setVisibility(View.GONE);
    loginDescription.setVisibility(View.VISIBLE);
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
    if (sendMagicLinkView.getVisibility() == View.VISIBLE) {
      sendMagicLinkView.setVisibility(View.GONE);
      loginSignupSelectionArea.setVisibility(View.VISIBLE);
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

  private void hideSocialLoginArea() {
    socialLoginArea.setVisibility(View.GONE);
  }

  private AccountAnalytics.StartupClickOrigin getStartupClickOrigin() {
    // TODO: Wtf is this?
    return AccountAnalytics.StartupClickOrigin.MAIN;
  }

  private void setAptoideLoginAreaVisible() {
    sendMagicLinkView.setVisibility(View.VISIBLE);
    termsAndConditions.setVisibility(View.GONE);
    termsConditionCheckBox.setVisibility(View.GONE);
    loginSignupSelectionArea.setVisibility(View.GONE);
    if (bottomSheetBehavior != null) {
      float newHeight = 320 * getResources().getDisplayMetrics().density;
      bottomSheetBehavior.setPeekHeight((int) newHeight);
    }
  }

  @NotNull @Override public Observable<String> getMagicLinkClick() {
    return sendMagicLinkView.getMagicLinkSubmit();
  }

  @Override public void setEmailInvalidError() {
    // TODO: HARDCODED STRING
    sendMagicLinkView.setState(new SendMagicLinkView.State.Error("The email isn't valid!", true));
  }

  @Override public void setExpiredMagicLinkError() {
    // TODO: HARDCODED STRING
    sendMagicLinkView.setState(new SendMagicLinkView.State.Error(
        "The link has expired. Send a new magic link to your email", false));
  }

  @Override public void setLoadingScreen() {
    showLoading();
  }

  @Override public void removeLoadingScreen() {
    hideKeyboard();
    hideLoading();
  }

  @Override public void setInitialState() {
    sendMagicLinkView.setState(SendMagicLinkView.State.Initial.INSTANCE);
  }

  @NotNull @Override public Observable<String> getEmailTextChangeEvent() {
    return sendMagicLinkView.getEmailChangeEvent();
  }
}
