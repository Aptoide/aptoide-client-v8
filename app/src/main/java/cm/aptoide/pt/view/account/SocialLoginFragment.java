/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.view.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.SocialLoginView;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.account.user.ManageUserFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 08/02/17.
 */

public abstract class SocialLoginFragment extends GooglePlayServicesFragment
    implements SocialLoginView {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private static final String TAG = SocialLoginFragment.class.getSimpleName();
  protected LoginManager facebookLoginManager;
  protected List<String> facebookRequestedPermissions;
  protected PublishRelay<FacebookAccountViewModel> facebookLoginSubject;
  protected boolean navigateToHome;
  protected boolean dismissToNavigateToMainView;
  protected CallbackManager callbackManager;
  private PublishRelay<GoogleAccountViewModel> googleLoginSubject;
  private GoogleApiClient client;
  private AccountErrorMapper errorMapper;
  private ProgressDialog progressDialog;
  private AptoideAccountManager accountManager;
  private CrashReport crashReport;
  private FragmentNavigator fragmentNavigator;
  private AlertDialog facebookEmailRequiredDialog;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    errorMapper = new AccountErrorMapper(getContext());
    facebookLoginManager = LoginManager.getInstance();
    facebookRequestedPermissions = Arrays.asList("email", "user_friends");
    callbackManager = CallbackManager.Factory.create();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    crashReport = CrashReport.getInstance();
    fragmentNavigator = getFragmentNavigator();
    if (!FacebookSdk.isInitialized()) {
      FacebookSdk.sdkInitialize(getContext());
    }
  }

  @Override public void onResume() {
    super.onResume();
    getGoogleButton().setOnClickListener(v -> {
      googleLoginClicked();
      connect();
      startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(client),
          RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE);
    });
  }

  @Override public void onPause() {
    super.onPause();
    getGoogleButton().setOnClickListener(null);
  }

  @Override public void showGoogleLogin() {
    getGoogleButton().setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    getGoogleButton().setVisibility(View.GONE);
  }

  public void showError(Throwable throwable) {
    Snackbar.make(getRootView(), errorMapper.map(throwable), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public Observable<Void> googleLoginClick() {
    return googleLoginSubject.doOnNext(selected -> showLoading()).<Void>flatMap(
        credentials -> accountManager.login(Account.Type.GOOGLE, credentials.getEmail(),
            credentials.getToken(), credentials.getDisplayName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              Logger.d(TAG, "google login successful");
              Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                  Analytics.Account.SignUpLoginStatus.SUCCESS,
                  Analytics.Account.LoginStatusDetail.SUCCESS);
              navigateToMainView();
            })
            .doOnTerminate(() -> hideLoading())
            .doOnError(throwable -> {
              showError(throwable);
              crashReport.log(throwable);
              Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                  Analytics.Account.SignUpLoginStatus.FAILED,
                  Analytics.Account.LoginStatusDetail.SDK_ERROR);
            })
            .toObservable()).retry();
  }

  @Override public Observable<Void> facebookLoginClick() {
    return facebookLoginSubject.doOnNext(
        __ -> Analytics.Account.clickIn(Analytics.Account.StartupClick.CONNECT_FACEBOOK,
            getStartupClickOrigin()))
        .doOnNext(selected -> showLoading()).<Void>flatMap(credentials -> {
          if (declinedRequiredPermissions(credentials.getDeniedPermissions())) {
            hideLoading();
            showPermissionsRequiredMessage();
            Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                Analytics.Account.SignUpLoginStatus.FAILED,
                Analytics.Account.LoginStatusDetail.PERMISSIONS_DENIED);
            return Observable.empty();
          }

          return getFacebookUsername(credentials.getToken()).flatMapCompletable(
              username -> accountManager.login(Account.Type.FACEBOOK, username,
                  credentials.getToken()
                      .getToken(), null)
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(() -> {
                    Logger.d(TAG, "facebook login successful");
                    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                        Analytics.Account.SignUpLoginStatus.SUCCESS,
                        Analytics.Account.LoginStatusDetail.SUCCESS);
                    navigateToMainView();
                  })
                  .doOnTerminate(() -> hideLoading())
                  .doOnError(throwable -> {
                    crashReport.log(throwable);
                    showError(throwable);
                  }))
              .toObservable();
        }).retry();
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public Single<String> getFacebookUsername(AccessToken accessToken) {
    return Single.create(singleSubscriber -> {
      final GraphRequest request =
          GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override public void onCompleted(JSONObject object, GraphResponse response) {
              if (!singleSubscriber.isUnsubscribed()) {
                if (response.getError() == null) {
                  String email = null;
                  try {
                    email =
                        object.has("email") ? object.getString("email") : object.getString("id");
                  } catch (JSONException e) {
                    singleSubscriber.onError(e);
                  }
                  singleSubscriber.onSuccess(email);
                } else {
                  singleSubscriber.onError(response.getError()
                      .getException());
                }
              }
            }
          });
      singleSubscriber.add(Subscriptions.create(() -> request.setCallback(null)));
      request.executeAsync();
    });
  }

  @Override public void navigateToMainView() {
    if (dismissToNavigateToMainView) {
      getActivity().finish();
    } else if (navigateToHome) {
      navigateToMainViewCleaningBackStack();
    } else {
      navigateBack();
    }
  }

  @Override public void navigateToCreateProfile() {
    fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateTo(ManageUserFragment.newInstanceToCreate());
  }

  @Override public void navigateToMainViewCleaningBackStack() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  @Override public void navigateBack() {
    fragmentNavigator.popBackStack();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE) {
      final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      final GoogleSignInAccount account = result.getSignInAccount();
      if (result.isSuccess() && account != null) {
        googleLoginSubject.call(
            new GoogleAccountViewModel(account.getDisplayName(), account.getServerAuthCode(),
                account.getEmail()));
      } else {
        showGoogleLoginError();
      }
    } else {
      callbackManager.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    googleLoginSubject = PublishRelay.create();
    client = ((AptoideApplication) getContext().getApplicationContext()).getGoogleSignInClient();
    bindViews(view);
  }

  @Override protected void connect() {
    client.connect();
  }

  public abstract Analytics.Account.StartupClickOrigin getStartupClickOrigin();

  public void googleLoginClicked() {
    // does nothing
  }

  protected abstract Button getGoogleButton();

  protected abstract Button getFacebookButton();

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequestedPermissions);
  }

  protected void bindViews(View view) {
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
    RxView.clicks(getFacebookButton())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> facebookLoginManager.logInWithReadPermissions(SocialLoginFragment.this,
            facebookRequestedPermissions));

    facebookEmailRequiredDialog = new AlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button, (dialog, which) -> {
          facebookLoginManager.logInWithReadPermissions(this, Arrays.asList("email"));
        })
        .setNegativeButton(android.R.string.cancel, null)
        .create();
  }

  private View getRootView() {
    return getActivity().findViewById(android.R.id.content);
  }

  protected void showFacebookLoginError(@StringRes int errorRes) {
    Snackbar.make(getRootView(), errorRes, Snackbar.LENGTH_LONG)
        .show();
  }

  protected void showGoogleLoginError() {
    Snackbar.make(getRootView(), R.string.google_login_cancelled, Snackbar.LENGTH_LONG)
        .show();
  }

  protected void registerFacebookCallback() {
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(),
            loginResult.getRecentlyDeniedPermissions()));
      }

      @Override public void onCancel() {
        showFacebookLoginError(R.string.facebook_login_cancelled);
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED, Analytics.Account.LoginStatusDetail.CANCEL);
      }

      @Override public void onError(FacebookException error) {
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED,
            Analytics.Account.LoginStatusDetail.SDK_ERROR);
        showFacebookLoginError(R.string.error_occured);
      }
    });
  }
}
