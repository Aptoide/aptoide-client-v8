package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreViewModel;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class ProfileStepOneFragment extends BaseToolbarFragment implements ProfileStepOneView {

  private Button continueBtn;
  private Button moreInfoBtn;
  private ProgressDialog waitDialog;
  private FragmentNavigator fragmentNavigator;
  private boolean externalLogin;

  public static ProfileStepOneFragment newInstance() {
    return new ProfileStepOneFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragmentNavigator = getFragmentNavigator();
    final Context context = getContext();
    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        context.getString(R.string.please_wait));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args != null) {
      externalLogin = args.getBoolean(AptoideAccountManager.IS_FACEBOOK_OR_GOOGLE, false);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(AptoideAccountManager.IS_FACEBOOK_OR_GOOGLE, externalLogin);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    loadExtras(savedInstanceState);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_profile_step_one;
  }

  @Override @NonNull public Observable<Boolean> continueButtonClick() {
    return RxView.clicks(continueBtn)
        .map(__ -> externalLogin);
  }

  @Override @NonNull public Observable<Void> moreInfoButtonClick() {
    return RxView.clicks(moreInfoBtn);
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

  @Override public void navigateToProfileStepTwoView() {
    fragmentNavigator.navigateTo(ProfileStepTwoFragment.newInstance());
  }

  @Override public void navigateToHome() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  @Override public void navigateToCreateStore() {
    fragmentNavigator.navigateToWithoutBackSave(
        ManageStoreFragment.newInstance(new ManageStoreViewModel(), true));
  }

  @Override public void showGenericErrorMessage() {
    ShowMessage.asSnack(this, R.string.unknown_error);
  }

  @Override public void setupViews() {
    super.setupViews();
    final Context applicationContext = getActivity().getApplicationContext();
    final AptoideAccountManager accountManager =
        ((V8Engine) applicationContext).getAccountManager();
    attachPresenter(new ProfileStepOnePresenter(this, CrashReport.getInstance(), accountManager),
        null);
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



