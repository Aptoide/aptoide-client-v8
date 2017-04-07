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
public class ProfileStepOneFragment extends BaseToolbarFragment {

  private AptoideAccountManager accountManager;

  private Button continueBtn;
  private Button moreInfoBtn;
  private ProgressDialog pleaseWaitDialog;
  private boolean externalLogin;

  public static ProfileStepOneFragment newInstance() {
    return new ProfileStepOneFragment();
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
    return R.layout.fragment_profile_step_one;
  }

  @Override public void setupViews() {
    super.setupViews();
    RxView.clicks(continueBtn)
        .doOnNext(__ -> pleaseWaitDialog.show())
        .flatMap(__ -> accountManager.updateAccount(Account.Access.PUBLIC)
            .andThen(showContinueSuccessMessage())
            .doOnCompleted(() -> {
              if (externalLogin) {
                navigateToHome();
                return;
              }
              navigateToCreateStore();
            })
            .toObservable())
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> {/**/}, err -> {
          CrashReport.getInstance().log(err);
          pleaseWaitDialog.dismiss();
          showErrorMessage();
        });

    RxView.clicks(moreInfoBtn).compose(bindUntilEvent(LifecycleEvent.DESTROY)).subscribe(__ -> {
      Analytics.Account.accountProfileAction(1, Analytics.Account.ProfileAction.MORE_INFO);
      navigateToProfileStepTwoView();
    }, err -> {
      CrashReport.getInstance().log(err);
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
    final ProfileStepTwoFragment fragment = ProfileStepTwoFragment.newInstance();
    getFragmentNavigator().navigateTo(fragment);
  }

  public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  private void navigateToCreateStore() {
    CreateStoreFragment fragment = CreateStoreFragment.newInstance();
    getFragmentNavigator().navigateTo(fragment);
  }

  private void showErrorMessage() {
    ShowMessage.asSnack(this, R.string.unknown_error);
  }

  private Completable showContinueSuccessMessage() {
    return Completable.fromCallable(() -> {
      pleaseWaitDialog.dismiss();
      return Completable.complete();
    }).andThen(ShowMessage.asObservableSnack(this, R.string.successful)).doOnCompleted(() -> {
      Analytics.Account.accountProfileAction(1, Analytics.Account.ProfileAction.CONTINUE);
    }).toCompletable();
  }
}



