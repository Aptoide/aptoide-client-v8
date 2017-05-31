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
import cm.aptoide.pt.v8engine.view.account.store.CreateStoreFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreModel;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.jakewharton.rxbinding.view.RxView;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

// TODO
// apply MVP
// save / restore data in input fields
public class ProfileStepOneFragment extends BaseToolbarFragment {

  private AptoideAccountManager accountManager;

  private Button continueBtn;
  private Button moreInfoBtn;
  private ProgressDialog waitDialog;
  private boolean externalLogin;
  private FragmentNavigator fragmentNavigator;

  public static ProfileStepOneFragment newInstance() {
    return new ProfileStepOneFragment();
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
    return R.layout.fragment_profile_step_one;
  }

  @Override public void setupViews() {
    super.setupViews();
    RxView.clicks(continueBtn)
        .doOnNext(__ -> waitDialog.show())
        .flatMapCompletable(__ -> accountManager.updateAccount(Account.Access.PUBLIC)
            .onErrorResumeNext(err -> {
              CrashReport.getInstance()
                  .log(err);
              return Completable.complete();
            })
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(showContinueSuccessMessage())
            .doOnCompleted(() -> {
              Analytics.Account.accountProfileAction(1, Analytics.Account.ProfileAction.CONTINUE);
            })
            .doOnCompleted(() -> {
              if (externalLogin) {
                navigateToHome();
                return;
              }
              navigateToCreateStore();
            }))
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance()
              .log(err);
          showErrorMessage();
        });

    RxView.clicks(moreInfoBtn)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> {
          Analytics.Account.accountProfileAction(1, Analytics.Account.ProfileAction.MORE_INFO);
          navigateToProfileStepTwoView();
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_profile_logged_in_activity_title);
  }

  public void bindViews(View view) {
    super.bindViews(view);
    continueBtn = (Button) view.findViewById(R.id.logged_in_continue);
    moreInfoBtn = (Button) view.findViewById(R.id.logged_in_more_info_button);
  }

  private void navigateToProfileStepTwoView() {
    waitDialog.dismiss();
    fragmentNavigator.navigateTo(ProfileStepTwoFragment.newInstance());
  }

  public void navigateToHome() {
    waitDialog.dismiss();
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateToCreateStore() {
    waitDialog.dismiss();
    fragmentNavigator.navigateTo(CreateStoreFragment.newInstance(new ManageStoreModel(true)));
  }

  private void showErrorMessage() {
    waitDialog.dismiss();
    ShowMessage.asSnack(this, R.string.unknown_error);
  }

  private Completable showContinueSuccessMessage() {
    return Completable.fromAction(() -> waitDialog.dismiss())
        .andThen(ShowMessage.asObservableSnack(this, R.string.title_successful)
            .takeUntil(visibility -> visibility == ShowMessage.DISMISSED))
        .toCompletable();
  }
}



