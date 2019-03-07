package cm.aptoide.pt.blacklist;

public class BlacklistUnitMapper {

  public Blacklister.BlacklistUnit mapToBlacklistUnit(String blacklistKey) {

    if (blacklistKey.equals("WALLET_ADS_OFFER_51")) {
      return Blacklister.BlacklistUnit.WALLET_ADS_OFFER;
    } else if (blacklistKey.equals("appc_card_info_1")) {
      return Blacklister.BlacklistUnit.APPC_CARD_INFO;
    } else {
      throw new IllegalArgumentException(
          "Wrong blacklist key. Please, make sure you are passing the correct type and id.");
    }
  }
}
