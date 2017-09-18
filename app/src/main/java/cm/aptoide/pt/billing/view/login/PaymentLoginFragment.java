package cm.aptoide.pt.billing.view.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.GooglePlayServicesFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import rx.Observable;

public class PaymentLoginFragment extends GooglePlayServicesFragment implements PaymentLoginView {

  private int requestCode;
  private ClickHandler handler;
  private PublishRelay<Void> backButtonRelay;
  private PublishRelay<Void> upNavigationRelay;
  private Button facebookButton;
  private Button googleButton;
  private ProgressDialog progressDialog;
  private AccountNavigator accountNavigator;
  private AptoideAccountManager accountManager;
  private CrashReport crashReport;
  private RxAlertDialog facebookEmailRequiredDialog;
  private View rootView;
  private AccountErrorMapper errorMapper;

  public static Fragment newInstance() {
    return new PaymentLoginFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestCode = getArguments().getInt(FragmentNavigator.REQUEST_CODE_EXTRA);
    backButtonRelay = PublishRelay.create();
    upNavigationRelay = PublishRelay.create();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    crashReport = CrashReport.getInstance();
    errorMapper = new AccountErrorMapper(getContext());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment_login, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setHasOptionsMenu(true);

    rootView = getActivity().findViewById(android.R.id.content);

    final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_payment_login_toolbar);
    ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getContext()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    facebookButton = (Button) view.findViewById(R.id.fragment_payment_login_facebook_button);
    googleButton = (Button) view.findViewById(R.id.fragment_payment_login_google_button);
    progressDialog = new ProgressDialog(getContext());
    progressDialog.setMessage(getString(cm.aptoide.pt.utils.R.string.please_wait));
    progressDialog.setCancelable(false);

    final Button button = (Button) view.findViewById(R.id.fragment_payment_login_join_button);
    button.setText(getString(R.string.join_company,
        ((AptoideApplication) getContext().getApplicationContext()).getMarketName()));

    handler = () -> {
      backButtonRelay.call(null);
      return true;
    };
    registerClickHandler(handler);

    attachPresenter(
        new PaymentLoginPresenter(this, requestCode, Arrays.asList("email", "user_friends"),
            accountNavigator, accountManager, crashReport, errorMapper), savedInstanceState);
  }

  @Override public Observable<Void> backButtonEvent() {
    return backButtonRelay;
  }

  @Override public Observable<Void> upNavigationEvent() {
    return upNavigationRelay;
  }

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookButton);
  }

  @Override public Observable<Void> googleSignUpEvent() {
    return RxView.clicks(googleButton);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(String message) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showFacebookPermissionsRequiredError(Throwable throwable) {
    if (!facebookEmailRequiredDialog.isShowing()) {
      facebookEmailRequiredDialog.show();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      upNavigationRelay.call(null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(handler);
    facebookEmailRequiredDialog.dismiss();
    facebookEmailRequiredDialog = null;
    facebookButton = null;
    progressDialog.dismiss();
    progressDialog = null;
    rootView = null;
    super.onDestroyView();
  }
}
