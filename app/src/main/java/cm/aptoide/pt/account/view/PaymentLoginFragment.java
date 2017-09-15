package cm.aptoide.pt.account.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.view.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public class PaymentLoginFragment extends GooglePlayServicesFragment implements PaymentLoginView {

  private int requestCode;
  private ClickHandler handler;
  private PublishRelay<Void> backButtonRelay;
  private PublishRelay<Void> upNavigationRelay;
  private BillingNavigator billingNavigator;

  public static Fragment newInstance() {
    return new PaymentLoginFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestCode = getArguments().getInt(FragmentNavigator.REQUEST_CODE_EXTRA);
    backButtonRelay = PublishRelay.create();
    upNavigationRelay = PublishRelay.create();
    billingNavigator = ((ActivityResultNavigator) getContext()).getBillingNavigator();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment_login, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setHasOptionsMenu(true);

    final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_payment_login_toolbar);
    ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getContext()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    final Button button = (Button) view.findViewById(R.id.fragment_payment_login_join_button);
    button.setText(getString(R.string.join_company,
        ((AptoideApplication) getContext().getApplicationContext()).getMarketName()));

    handler = () -> {
      backButtonRelay.call(null);
      return true;
    };
    registerClickHandler(handler);

    attachPresenter(new PaymentLoginPresenter(this, billingNavigator, requestCode),
        savedInstanceState);
  }

  @Override public Observable<Void> backButtonEvents() {
    return backButtonRelay;
  }

  @Override public Observable<Void> upNavigationEvents() {
    return upNavigationRelay;
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
    super.onDestroyView();
  }
}
