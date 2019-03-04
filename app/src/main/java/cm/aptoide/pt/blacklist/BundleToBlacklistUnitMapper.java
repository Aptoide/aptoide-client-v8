package cm.aptoide.pt.blacklist;

import cm.aptoide.pt.home.ActionBundle;

public class BundleToBlacklistUnitMapper {

  public BlacklistManager.BlacklistUnit mapBundleToBlacklistUnit(ActionBundle bundle,
      String cardId) {

    String blacklistKey = bundle.getType()
        .toString() + "_" + cardId;

    if (blacklistKey.equals("WALLET_ADS_OFFER_51")) {
      return BlacklistManager.BlacklistUnit.WALLET_ADS_OFFER;
    } else {
      return null;
    }
  }
}
