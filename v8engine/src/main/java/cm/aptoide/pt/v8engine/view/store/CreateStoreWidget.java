package cm.aptoide.pt.v8engine.view.store;

import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.account.user.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private final CrashReport crashReport;
  private Button button;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;

  public CreateStoreWidget(View itemView) {
    super(itemView);
    crashReport = CrashReport.getInstance();
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = new AccountNavigator(getFragmentNavigator(), accountManager);

    compositeSubscription.add(RxView.clicks(button)
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(account -> {
          if (account.isLoggedIn()) {
            button.setText(R.string.create_store_displayable_button);
            getFragmentNavigator().navigateTo(
                ManageStoreFragment.newInstance(new ManageStoreFragment.ViewModel(), false));
          } else {
            button.setText(R.string.login);
            accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.STORE);
          }
        })
        .subscribe(__ -> {
        }, err -> crashReport.log(err)));
  }
}
