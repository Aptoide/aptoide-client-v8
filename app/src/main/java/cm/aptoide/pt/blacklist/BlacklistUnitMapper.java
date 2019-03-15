package cm.aptoide.pt.blacklist;

public class BlacklistUnitMapper {

  public BlacklistManager.BlacklistUnit mapToBlacklistUnit(String blacklistKey) {

    switch (blacklistKey) {
      case "WALLET_ADS_OFFER_51":
        return BlacklistManager.BlacklistUnit.WALLET_ADS_OFFER;
      case "appc_card_info_1":
        return BlacklistManager.BlacklistUnit.APPC_CARD_INFO;
      default:
        throw new IllegalArgumentException(
            "Wrong blacklist key. Please, make sure you are passing the correct type and id.");
    }
  }
}
