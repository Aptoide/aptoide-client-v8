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
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreViewModel;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.jakewharton.rxbinding.view.RxView;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

// TODO
// apply MVP
// save / restore data in input fields
public class ProfileStepTwoFragment extends BaseToolbarFragment {

  private Button continueBtn;
  private Button privateProfileBtn;
  private ProgressDialog waitDialog;
  private AptoideAccountManager accountManager;
  private boolean externalLogin;
  private FragmentNavigator fragmentNavigator;

  public static ProfileStepTwoFragment newInstance() {
    return new ProfileStepTwoFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Context applicationContext = getActivity().getApplicationContext();
    fragmentNavigator = getFragmentNavigator();
    accountManager = ((V8Engine) applicationContext).getAccountManager();
    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        applicationContext.getString(R.string.please_wait));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
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
        .doOnNext(click -> waitDialog.show())
        .flatMapCompletable(click -> accountManager.updateAccount(Account.Access.PUBLIC)
            .onErrorResumeNext(err -> {
              CrashReport.getInstance()
                  .log(err);
              return showErrorMessage();
            })
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(showContinueSuccessMessage())
            .doOnCompleted(() -> sendAnalytics(Analytics.Account.ProfileAction.CONTINUE))
            .doOnCompleted(() -> navigateToCreateStoreOrHome()))
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();

    RxView.clicks(privateProfileBtn)
        .doOnNext(__ -> waitDialog.show())
        .flatMapCompletable(__ -> accountManager.updateAccount(Account.Access.UNLISTED)
            .onErrorResumeNext(err -> {
              CrashReport.getInstance()
                  .log(err);
              return showErrorMessage();
            })
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(showContinueSuccessMessage())
            .doOnCompleted(() -> sendAnalytics(Analytics.Account.ProfileAction.PRIVATE_PROFILE))
            .doOnCompleted(() -> navigateToCreateStoreOrHome()))
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
    return Completable.fromAction(() -> waitDialog.dismiss())
        .andThen(ShowMessage.asObservableSnack(this, R.string.unknown_error)
            .toCompletable());
  }

  private Completable showContinueSuccessMessage() {
    return Completable.fromAction(() -> waitDialog.dismiss())
        .andThen(ShowMessage.asObservableSnack(this, R.string.title_successful)
            .takeUntil(visibility -> visibility == ShowMessage.DISMISSED))
        .toCompletable();
  }

  private Completable sendAnalytics(Analytics.Account.ProfileAction action) {
    return Completable.fromAction(() -> Analytics.Account.accountProfileAction(2, action));
  }

  private void navigateToCreateStoreOrHome() {
    waitDialog.dismiss();
    if (externalLogin) {
      fragmentNavigator.navigateToHomeCleaningBackStack();
      return;
    }
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(new ManageStoreViewModel(), true));
  }
}
