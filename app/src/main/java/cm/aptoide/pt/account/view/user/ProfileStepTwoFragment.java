package cm.aptoide.pt.account.view.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.LoginSignupManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Completable;
import rx.Observable;

public class ProfileStepTwoFragment extends BaseToolbarFragment
    implements ProfileStepTwoView, NotBottomNavigationView {

  @LayoutRes private static final int LAYOUT = R.layout.fragment_profile_step_two;
  @Inject AccountAnalytics accountAnalytics;
  @Inject LoginSignupManager loginSignupManager;
  @Inject MarketResourceFormatter marketResourceFormatter;
  @Inject ThemeManager themeManager;
  private Button continueBtn;
  private Button privateProfileBtn;
  private TextView createProfileTitle;
  private ProgressDialog waitDialog;
  private boolean externalLogin;
  private AccountNavigator accountNavigator;

  public static ProfileStepTwoFragment newInstance() {
    return new ProfileStepTwoFragment();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId,
        getContext().getString(R.string.please_wait));
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args != null) {
      externalLogin = args.getBoolean(ProfileStepOneFragment.IS_EXTERNAL_LOGIN, false);
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
    outState.putBoolean(ProfileStepOneFragment.IS_EXTERNAL_LOGIN, externalLogin);
  }

  @Override public int getContentViewId() {
    return LAYOUT;
  }

  @Override public Observable<Boolean> continueButtonClick() {
    return RxView.clicks(continueBtn)
        .map(__ -> externalLogin);
  }

  @Override public Observable<Boolean> makePrivateProfileButtonClick() {
    return RxView.clicks(privateProfileBtn)
        .map(__ -> externalLogin);
  }

  @Override public void showWaitDialog() {
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitDialog() {
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public Completable showGenericErrorMessage() {
    return Completable.fromAction(() -> waitDialog.dismiss())
        .andThen(ShowMessage.asLongObservableSnack(this, R.string.unknown_error));
  }

  @Override public void setupViews() {
    super.setupViews();
    final Context applicationContext = getContext().getApplicationContext();
    final AptoideAccountManager accountManager =
        ((AptoideApplication) applicationContext).getAccountManager();
    ProfileStepTwoPresenter presenter =
        new ProfileStepTwoPresenter(this, accountManager, CrashReport.getInstance(),
            accountNavigator, accountAnalytics, loginSignupManager);
    attachPresenter(presenter);
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_profile_logged_in_activity_title);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    continueBtn = (Button) view.findViewById(R.id.logged_in_continue);
    privateProfileBtn = (Button) view.findViewById(R.id.logged_in_private_button);
    createProfileTitle = view.findViewById(R.id.create_profile_title);
    createProfileTitle.setText(
        marketResourceFormatter.formatString(getContext(), R.string.create_profile_pub_pri));
  }
}
