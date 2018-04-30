package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class AppViewRewardAppDisplayable extends AppViewDisplayable {

  private double appcoinsReward;

  public AppViewRewardAppDisplayable() {
  }

  public AppViewRewardAppDisplayable(double appcoinsReward) {
    this.appcoinsReward = appcoinsReward;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_appcoins_reward;
  }

  public double getAppcoinsReward() {
    return appcoinsReward;
  }
}
