package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.v8engine.view.JoinCommunityView;
import cm.aptoide.pt.v8engine.view.View;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

public class JoinCommunityPresenter implements Presenter {

  private final JoinCommunityView view;
  private final AptoideAccountManager accountManager;
  private final List<String> facebookRequestedPermissions;

  public JoinCommunityPresenter(JoinCommunityView view, AptoideAccountManager accountManager,
      List<String> facebookRequestedPermissions) {
    this.view = view;
    this.accountManager = accountManager;
    this.facebookRequestedPermissions = facebookRequestedPermissions;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> showOrHideLogins())
        .flatMap(resumed -> Observable.merge(googleLoginClick(), facebookLoginClick(),
            aptoideShowLoginClick(), aptoideShowSignUpClick()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> successMessageShown().compose(
            view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showOrHideLogins() {
    showOrHideFacebookLogin();
    showOrHideGoogleLogin();
  }

  private Observable<Void> googleLoginClick() {
    return view.googleLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> accountManager.login(LoginMode.GOOGLE, credentials.getEmail(),
            credentials.getToken(), credentials.getDisplayName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> view.showSuccessMessage())
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> view.showError(throwable))
            .toObservable()).retry();
  }

  private Observable<Void> facebookLoginClick() {
    return view.facebookLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> {
          if (declinedRequiredPermissions(credentials.getDeniedPermissions())) {
            view.hideLoading();
            view.showPermissionsRequiredMessage();
            return Observable.empty();
          }

          return getFacebookUsername(credentials.getToken()).flatMapCompletable(
              username -> accountManager.login(LoginMode.FACEBOOK, username,
                  credentials.getToken().getToken(), null)
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(() -> view.showSuccessMessage())
                  .doOnTerminate(() -> view.hideLoading())
                  .doOnError(throwable -> view.showError(throwable))).toObservable();
        }).retry();
  }

  private Observable<Void> aptoideShowLoginClick() {
    return view.showAptoideLoginClick().doOnNext(__ -> view.setLoginAreaVisible());
  }

  private Observable<Void> aptoideShowSignUpClick() {
    return view.showSignUpClick().doOnNext(__ -> view.setSignUpAreaVisible());
  }

  private Observable<Void> successMessageShown() {
    return view.successMessageShown().doOnNext(selection -> view.navigateToMainView());
  }

  private void showOrHideFacebookLogin() {
    if (accountManager.isFacebookLoginEnabled()) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private void showOrHideGoogleLogin() {
    if (accountManager.isGoogleLoginEnabled()) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequestedPermissions);
  }

  private Single<String> getFacebookUsername(AccessToken accessToken) {
    return Single.create(new Single.OnSubscribe<String>() {
      @Override public void call(SingleSubscriber<? super String> singleSubscriber) {
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
                    singleSubscriber.onError(response.getError().getException());
                  }
                }
              }
            });
        singleSubscriber.add(Subscriptions.create(() -> request.setCallback(null)));
        request.executeAsync();
      }
    });
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}
