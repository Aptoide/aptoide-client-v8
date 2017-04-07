package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.Completable;

// FIXME
// refactor (remove) more code
// chain Rx in method calls
// apply MVP
// save / restore data in input fields
public class ProfileStepTwoFragment extends BaseToolbarFragment {

  private Button continueBtn;
  private Button privateProfileBtn;
  private ProgressDialog pleaseWaitDialog;
  private AptoideAccountManager accountManager;
  private boolean externalLogin;

  public static ProfileStepTwoFragment newInstance() {
    return new ProfileStepTwoFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Context applicationContext = getActivity().getApplicationContext();
    accountManager = ((V8Engine) applicationContext).getAccountManager();
    pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        applicationContext.getString(R.string.please_wait));
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    externalLogin = args.getBoolean(AptoideAccountManager.IS_FACEBOOK_OR_GOOGLE, false);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_profile_step_two;
  }

  @Override public void setupViews() {
    super.setupViews();

    RxView.clicks(continueBtn)
        .doOnNext(click -> pleaseWaitDialog.show())
        .flatMap(click -> accountManager.updateAccount(Account.Access.PUBLIC)
            .andThen(showContinueSuccessMessage(Analytics.Account.ProfileAction.CONTINUE))
            .onErrorResumeNext(err -> {
              CrashReport.getInstance().log(err);
              return showErrorMessage();
            })
            .doOnCompleted(() -> navigateToCreateStoreViewOrDismiss())
            .toObservable())
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();

    RxView.clicks(privateProfileBtn)
        .doOnNext(click -> pleaseWaitDialog.show())
        .flatMap(click -> accountManager.updateAccount(Account.Access.UNLISTED)
            .doOnCompleted(
                () -> showContinueSuccessMessage(Analytics.Account.ProfileAction.PRIVATE_PROFILE))
            .onErrorResumeNext(err -> {
              CrashReport.getInstance().log(err);
              return showErrorMessage();
            })
            .doOnCompleted(() -> navigateToCreateStoreViewOrDismiss())
            .toObservable())
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_profile_logged_in_activity_title);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    continueBtn = (Button) view.findViewById(R.id.logged_in_continue);
    privateProfileBtn = (Button) view.findViewById(R.id.logged_in_private_button);
  }

  private Completable showErrorMessage() {
    return ShowMessage.asObservableSnack(this, R.string.unknown_error)
        .filter(vis -> vis == ShowMessage.DISMISSED)
        .toCompletable();
  }

  private Completable showContinueSuccessMessage(Analytics.Account.ProfileAction action) {
    return ShowMessage.asObservableSnack(this, R.string.successful)
        .filter(vis -> vis == ShowMessage.DISMISSED)
        .toCompletable()
        .andThen(sendAnalytics(action));
  }

  private Completable sendAnalytics(Analytics.Account.ProfileAction action) {
    return Completable.fromAction(() -> Analytics.Account.accountProfileAction(2, action));
  }

  private void navigateToCreateStoreViewOrDismiss() {
    if (externalLogin) {
      navigateToHome();
      return;
    }

    navigateToCreateStore();
  }

  private void navigateToCreateStore() {
    getFragmentNavigator().navigateTo(CreateStoreFragment.newInstance());
  }

  public void navigateToHome() {
    pleaseWaitDialog.dismiss();
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }
}
