package cm.aptoide.pt.account.view.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.LoginSignupManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Completable;
import rx.Observable;

public class ProfileStepOneFragment extends BaseToolbarFragment
    implements ProfileStepOneView, NotBottomNavigationView {

  public static final String IS_EXTERNAL_LOGIN = "facebook_google";
  @LayoutRes private static final int LAYOUT = R.layout.fragment_profile_step_one;

  @Inject ScreenOrientationManager orientationManager;
  @Inject AccountNavigator accountNavigator;
  @Inject AccountAnalytics accountAnalytics;
  @Inject LoginSignupManager loginSignupManager;
  private Button continueBtn;
  private Button moreInfoBtn;
  private ProgressDialog waitDialog;
  private boolean externalLogin;

  public static ProfileStepOneFragment newInstance() {
    return new ProfileStepOneFragment();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        getContext().getString(R.string.please_wait));
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args != null) {
      externalLogin = args.getBoolean(IS_EXTERNAL_LOGIN, false);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    loadExtras(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(IS_EXTERNAL_LOGIN, externalLogin);
  }

  @Override public int getContentViewId() {
    return LAYOUT;
  }

  @Override @NonNull public Observable<Boolean> continueButtonClick() {
    return RxView.clicks(continueBtn)
        .map(__ -> externalLogin);
  }

  @Override @NonNull public Observable<Void> moreInfoButtonClick() {
    return RxView.clicks(moreInfoBtn);
  }

  @Override public void showWaitDialog() {
    orientationManager.lock();
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitDialog() {
    orientationManager.unlock();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public Completable showGenericErrorMessage() {
    return ShowMessage.asLongObservableSnack(this, R.string.unknown_error);
  }

  @Override public void setupViews() {
    super.setupViews();
    final Context applicationContext = getActivity().getApplicationContext();
    final AptoideAccountManager accountManager =
        ((AptoideApplication) applicationContext).getAccountManager();
    attachPresenter(new ProfileStepOnePresenter(this, CrashReport.getInstance(), accountManager,
        accountNavigator, accountAnalytics, loginSignupManager));
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_profile_logged_in_activity_title);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    continueBtn = (Button) view.findViewById(R.id.logged_in_continue);
    moreInfoBtn = (Button) view.findViewById(R.id.logged_in_more_info_button);
  }
}



