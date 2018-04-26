package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

/**
 * Created by filipegoncalves on 4/26/18.
 */

public class AppCoinsRewardApp extends App {

  private AppCoins appcoins;

  public AppCoinsRewardApp() {
  }

  public AppCoins getAppcoins() {
    return appcoins;
  }

  public void setAppcoins(AppCoins appcoins) {
    this.appcoins = appcoins;
  }

  public static class AppCoins {
    public double reward;

    public AppCoins() {
    }

    public double getReward() {
      return reward;
    }

    public void setReward(double reward) {
      this.reward = reward;
    }
  }
}
