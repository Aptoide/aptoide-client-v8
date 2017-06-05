package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.Completable;
import rx.Observable;

public class ProfileStepTwoFragment extends BaseToolbarFragment implements ProfileStepTwoView {

  private Button continueBtn;
  private Button privateProfileBtn;
  private ProgressDialog waitDialog;
  private boolean externalLogin;

  public static ProfileStepTwoFragment newInstance() {
    return new ProfileStepTwoFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext(),
        getContext().getString(R.string.please_wait));
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
    return R.layout.fragment_profile_step_two;
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
        ((V8Engine) applicationContext).getAccountManager();
    ProfileStepTwoPresenter presenter =
        new ProfileStepTwoPresenter(this, accountManager, CrashReport.getInstance(),
            getFragmentNavigator());
    attachPresenter(presenter, null);
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
}
