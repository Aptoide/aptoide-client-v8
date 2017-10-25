package cm.aptoide.pt.store.view;

import android.support.annotation.NonNull;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.account.view.LoginDisplayable;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 13/09/2017.
 */

public class LoginWidget extends Widget<LoginDisplayable> {

  private View loginButton;

  public LoginWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    loginButton = itemView.findViewById(R.id.login_button);
  }

  @Override public void bindView(LoginDisplayable displayable) {

    final AccountNavigator accountNavigator =
        ((ActivityResultNavigator) getContext()).getAccountNavigator();

    compositeSubscription.add(RxView.clicks(loginButton)
        .subscribe(
            click -> accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.STORE),
            throwable -> CrashReport.getInstance()
                .log(throwable)));
  }
}
