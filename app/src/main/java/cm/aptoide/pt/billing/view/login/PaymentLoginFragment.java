package cm.aptoide.pt.billing.view.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.GooglePlayServicesFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PaymentLoginFragment extends GooglePlayServicesFragment implements PaymentLoginView {

  private static final String EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.USERNAME_PASSWORD_CONTAINER_VISIBLE";
  private static final String EXTRA_LOGIN_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.LOGIN_VISIBLE ";
  private static final String EXTRA_PASSWORD_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.PASSWORD_VISIBLE";
  private static final String EXTRA_FACEBOOK_DIALOG_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.FACEBOOK_DIALOG_VISIBLE";
  private static final String EXTRA_PROGRESS_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.PROGRESS_VISIBLE";
  @Inject PaymentLoginPresenter paymentLoginPresenter;
  @Inject @Named("marketName") String marketName;
  private ClickHandler handler;
  private PublishRelay<Void> backButtonRelay;
  private PublishRelay<Void> upNavigationRelay;
  private PublishRelay<Void> passwordKeyboardGoRelay;
  private Button facebookButton;
  private Button googleButton;
  private ProgressDialog progressDialog;
  private RxAlertDialog facebookEmailRequiredDialog;
  private View rootView;
  private View aptoideLoginSignUpSeparator;
  private View aptoideLoginSignUpButtonContainer;
  private Button aptoideJoinToggle;
  private Button aptoideLoginToggle;
  private View usernamePasswordContainer;
  private View aptoideSignUpContainer;
  private View aptoideLoginContainer;
  private boolean usernamePasswordContainerVisible;
  private boolean loginVisible;
  private View recoverPasswordButton;
  private Button aptoideLoginButton;
  private Button aptoideSignUpButton;
  private EditText usernameEditText;
  private EditText passwordEditText;
  private Button passwordShowHideToggle;
  private boolean passwordVisible;
  private boolean progressVisible;
  private boolean facebookEmailRequiredDialogVisible;
  private PublishSubject<Void> privacyPolicySubject;
  private PublishSubject<Void> termsAndConditionsSubject;
  private TextView termsAndConditions;
  private CheckBox termsConditionCheckBox;

  public static Fragment newInstance() {
    return new PaymentLoginFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    backButtonRelay = PublishRelay.create();
    upNavigationRelay = PublishRelay.create();
    passwordKeyboardGoRelay = PublishRelay.create();
    privacyPolicySubject = PublishSubject.create();
    termsAndConditionsSubject = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onResume() {
    super.onResume();
    facebookEmailRequiredDialog.dismisses()
        .doOnNext(__ -> facebookEmailRequiredDialogVisible = false)
        .compose(bindUntilEvent(FragmentEvent.PAUSE))
        .subscribe();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment_login, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE,
        usernamePasswordContainerVisible);
    outState.putBoolean(EXTRA_LOGIN_VISIBLE, loginVisible);
    outState.putBoolean(EXTRA_PASSWORD_VISIBLE, passwordVisible);
    outState.putBoolean(EXTRA_FACEBOOK_DIALOG_VISIBLE, facebookEmailRequiredDialogVisible);
    outState.putBoolean(EXTRA_PROGRESS_VISIBLE, progressVisible);
    super.onSaveInstanceState(outState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rootView = getActivity().findViewById(android.R.id.content);

    final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_payment_login_toolbar);
    ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getContext()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    aptoideLoginSignUpSeparator =
        view.findViewById(R.id.fragment_payment_login_aptoide_buttons_separator_container);

    aptoideLoginSignUpButtonContainer =
        view.findViewById(R.id.fragment_payment_login_aptoide_buttons_container);

    aptoideSignUpContainer = view.findViewById(R.id.fragment_payment_sign_up_container);
    aptoideLoginContainer = view.findViewById(R.id.fragment_payment_login_container);

    aptoideJoinToggle = (Button) view.findViewById(R.id.fragment_payment_login_join_button);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    if ("vanilla".equalsIgnoreCase(BuildConfig.FLAVOR_product)) {
      aptoideJoinToggle.setText(getString(R.string.onboarding_button_join_us));
    } else {
      aptoideJoinToggle.setText(getString(R.string.join_company, marketName));
    }
    aptoideLoginToggle = (Button) view.findViewById(R.id.fragment_payment_login_small_button);
    recoverPasswordButton = view.findViewById(R.id.fragment_payment_login_recover_password_button);
    aptoideLoginButton = (Button) view.findViewById(R.id.fragment_payment_login_large_login_button);
    aptoideSignUpButton = (Button) view.findViewById(R.id.fragment_payment_login_sign_up_button);
    usernameEditText = (EditText) view.findViewById(R.id.fragment_payment_login_username);
    passwordEditText = (EditText) view.findViewById(R.id.fragment_payment_login_password);
    passwordShowHideToggle =
        (Button) view.findViewById(R.id.fragment_payment_login_show_hide_pasword_button);

    usernamePasswordContainer =
        view.findViewById(R.id.fragment_payment_login_username_password_container);

    facebookButton = (Button) view.findViewById(R.id.fragment_payment_login_facebook_button);
    googleButton = (Button) view.findViewById(R.id.fragment_payment_login_google_button);
    progressDialog = new ProgressDialog(getContext());
    progressDialog.setMessage(getString(cm.aptoide.pt.utils.R.string.please_wait));
    progressDialog.setCancelable(false);
    termsConditionCheckBox = (CheckBox) view.findViewById(R.id.tc_checkbox);

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

    RxView.clicks(aptoideJoinToggle)
        .doOnNext(__ -> {
          if (termsConditionCheckBox.isChecked()) {
            showUsernamePasswordContainer(false);
          } else {
            showTermsConditionError();
          }
        })
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    RxView.clicks(aptoideLoginToggle)
        .doOnNext(__ -> showUsernamePasswordContainer(true))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    RxView.clicks(passwordShowHideToggle)
        .doOnNext(__ -> togglePasswordVisibility(!passwordVisible))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    RxTextView.editorActionEvents(passwordEditText)
        .filter(event -> event.actionId() == EditorInfo.IME_ACTION_GO)
        .doOnNext(__ -> passwordKeyboardGoRelay.call(null))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    handler = () -> {
      backButtonRelay.call(null);
      return true;
    };
    registerClickHandler(handler);

    if (savedInstanceState != null) {

      if (savedInstanceState.getBoolean(EXTRA_FACEBOOK_DIALOG_VISIBLE)) {
        showFacebookPermissionsRequiredError();
      }

      if (savedInstanceState.getBoolean(EXTRA_PROGRESS_VISIBLE)) {
        showLoading();
      }

      if (savedInstanceState.getBoolean(EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE)) {
        showUsernamePasswordContainer(savedInstanceState.getBoolean(EXTRA_LOGIN_VISIBLE));
      } else {
        hideUsernamePasswordContainer();
      }
      togglePasswordVisibility(savedInstanceState.getBoolean(EXTRA_PASSWORD_VISIBLE));
    }

    attachPresenter(paymentLoginPresenter);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(handler);
    facebookEmailRequiredDialog.dismiss();
    facebookEmailRequiredDialog = null;
    facebookButton = null;
    googleButton = null;
    progressDialog.dismiss();
    progressDialog = null;
    rootView = null;
    aptoideLoginSignUpSeparator = null;
    aptoideLoginSignUpButtonContainer = null;
    aptoideSignUpContainer = null;
    aptoideLoginContainer = null;
    aptoideJoinToggle = null;
    aptoideLoginToggle = null;
    usernamePasswordContainer = null;
    recoverPasswordButton = null;
    aptoideLoginButton = null;
    aptoideSignUpButton = null;
    usernameEditText = null;
    passwordEditText = null;
    passwordShowHideToggle = null;
    termsAndConditions = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    privacyPolicySubject = null;
    termsAndConditionsSubject = null;
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      upNavigationRelay.call(null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public Observable<Void> backButtonEvent() {
    return backButtonRelay.filter(__ -> {
      if (usernamePasswordContainer.getVisibility() == View.VISIBLE) {
        hideUsernamePasswordContainer();
        return false;
      }
      return true;
    });
  }

  @Override public Observable<Void> upNavigationEvent() {
    return upNavigationRelay;
  }

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookButton);
  }

  @Override public Observable<Void> googleSignUpEvent() {
    return RxView.clicks(googleButton);
  }

  @Override public Observable<Void> recoverPasswordEvent() {
    return RxView.clicks(recoverPasswordButton);
  }

  @Override public Observable<Void> termsAndConditionsClickEvent() {
    return termsAndConditionsSubject;
  }

  @Override public Observable<Void> privacyPolicyClickEvent() {
    return privacyPolicySubject;
  }

  @Override public Observable<AptoideCredentials> aptoideLoginEvent() {
    return Observable.merge(RxView.clicks(aptoideLoginButton),
        passwordKeyboardGoRelay.filter(__ -> loginVisible))
        .doOnNext(__ -> hideKeyboard())
        .map(__ -> new AptoideCredentials(usernameEditText.getText()
            .toString(), passwordEditText.getText()
            .toString(), termsConditionCheckBox.isChecked()));
  }

  @Override public Observable<AptoideCredentials> aptoideSignUpEvent() {
    return Observable.merge(RxView.clicks(aptoideSignUpButton),
        passwordKeyboardGoRelay.filter(__ -> !loginVisible))
        .doOnNext(__ -> hideKeyboard())
        .map(__ -> new AptoideCredentials(usernameEditText.getText()
            .toString(), passwordEditText.getText()
            .toString(), termsConditionCheckBox.isChecked()));
  }

  @Override public Observable<Void> grantFacebookRequiredPermissionsEvent() {
    return facebookEmailRequiredDialog.positiveClicks()
        .map(dialogInterface -> null);
  }

  @Override public void showLoading() {
    progressVisible = true;
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressVisible = false;
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
  }

  @Override public void showFacebookPermissionsRequiredError() {
    if (!facebookEmailRequiredDialog.isShowing()) {
      facebookEmailRequiredDialogVisible = true;
      facebookEmailRequiredDialog.show();
    }
  }

  private void showUsernamePasswordContainer(boolean showLogin) {
    usernamePasswordContainer.setVisibility(View.VISIBLE);
    usernamePasswordContainerVisible = true;

    if (showLogin) {
      loginVisible = true;
      aptoideLoginContainer.setVisibility(View.VISIBLE);
      aptoideSignUpContainer.setVisibility(View.GONE);
      termsConditionCheckBox.setVisibility(View.GONE);
      termsAndConditions.setVisibility(View.GONE);
    } else {
      loginVisible = false;
      aptoideLoginContainer.setVisibility(View.GONE);
      aptoideSignUpContainer.setVisibility(View.VISIBLE);
      termsConditionCheckBox.setVisibility(View.VISIBLE);
      termsAndConditions.setVisibility(View.VISIBLE);
    }

    aptoideLoginSignUpSeparator.setVisibility(View.GONE);
    aptoideLoginSignUpButtonContainer.setVisibility(View.GONE);
  }

  private void hideUsernamePasswordContainer() {
    aptoideLoginSignUpSeparator.setVisibility(View.VISIBLE);
    aptoideLoginSignUpButtonContainer.setVisibility(View.VISIBLE);
    termsConditionCheckBox.setVisibility(View.VISIBLE);
    termsAndConditions.setVisibility(View.VISIBLE);
    usernamePasswordContainer.setVisibility(View.GONE);
    aptoideLoginContainer.setVisibility(View.GONE);
    aptoideSignUpContainer.setVisibility(View.GONE);
    usernamePasswordContainerVisible = false;
  }

  private void togglePasswordVisibility(boolean showPassword) {
    if (showPassword) {
      passwordEditText.setTransformationMethod(null);
      passwordShowHideToggle.setBackgroundResource(R.drawable.ic_open_eye);
      passwordVisible = true;
    } else {
      passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
      passwordShowHideToggle.setBackgroundResource(R.drawable.ic_closed_eye);
      passwordVisible = false;
    }
  }
}
