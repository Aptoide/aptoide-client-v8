package cm.aptoide.pt.spotandshareapp;

import java.util.List;

/**
 * Created by filipe on 22-09-2017.
 */

public class SpotAndShareTransfer {
  private SpotAndShareUser senderUser;
  private List<TransferAppModel> appsList;

  public SpotAndShareTransfer(SpotAndShareUser senderUser, List<TransferAppModel> appsList) {
    this.senderUser = senderUser;
    this.appsList = appsList;
  }

  public SpotAndShareUser getSenderUser() {
    return senderUser;
  }

  public List<TransferAppModel> getAppsList() {
    return appsList;
  }
}
