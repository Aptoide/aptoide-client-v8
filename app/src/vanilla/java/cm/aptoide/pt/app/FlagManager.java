package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import rx.Single;

/**
 * Created by D01 on 08/05/18.
 */

public class FlagManager {

  private final FlagService flagService;

  public FlagManager(FlagService flagService) {

    this.flagService = flagService;
  }

  public Single<GenericResponseV2> loadAddApkFlagRequest(String storeName, String md5,
      String flag) {
    return flagService.loadAddApkFlagRequest(storeName, md5, flag);
  }
}
