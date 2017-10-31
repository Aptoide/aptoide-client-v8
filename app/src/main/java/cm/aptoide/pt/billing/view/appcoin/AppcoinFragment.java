package cm.aptoide.pt.billing.view.appcoin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.billing.view.PaymentActivity;
import cm.aptoide.pt.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.view.fragment.FragmentView;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jose_messejana on 27-10-2017.
 */

public class AppcoinFragment extends FragmentView implements cm.aptoide.pt.presenter.View {

  private Billing billing;
  private AptoideAccountManager accountManager;
  private String marketName;
  private BillingNavigator billingNavigator;
  private Button proceedTransaction;
  private EditText receiverAddress;
  private BillingAnalytics billingAnalytics;
  //private EditText amount;
  private PublishSubject<String> publishSubject;

  public static Fragment create(Bundle bundle) {
    final AppcoinFragment fragment = new AppcoinFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling();
    accountManager = ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    billingAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    marketName = ((AptoideApplication) getContext().getApplicationContext()).getMarketName();
    publishSubject = PublishSubject.create();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.appcoins_fragment, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    receiverAddress = (EditText) view.findViewById(R.id.receiverAddress);
   // amount = (EditText) view.findViewById(R.id.amount);
    proceedTransaction = (Button) view.findViewById(R.id.ok_appcoin);
    proceedTransaction.setOnClickListener(click -> publishSubject.onNext(receiverAddress.getText().toString()));
    attachPresenter(new AppcoinPresenter(this, billing, new BillingNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
        getActivityNavigator(), getFragmentNavigator(), marketName),
        getArguments().getString(PaymentActivity.EXTRA_APPLICATION_ID),
        getArguments().getString(PaymentActivity.EXTRA_PRODUCT_ID)), savedInstanceState);
  }

  public Observable<String> proceedTransaction() {
    return publishSubject;
  }


}
