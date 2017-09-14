package cm.aptoide.pt.timeline.view.login;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 21/12/2016.
 */

public class TimelineLoginDisplayable extends Displayable {

  private AccountNavigator accountNavigator;

  public TimelineLoginDisplayable() {
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_login_header_layout;
  }

  public void login() {
    accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.TIMELINE);
  }

  public TimelineLoginDisplayable setAccountNavigator(AccountNavigator accountNavigator) {
    this.accountNavigator = accountNavigator;
    return this;
  }
}
