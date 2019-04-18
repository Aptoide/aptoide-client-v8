package cm.aptoide.pt.blacklist;

public class BlacklistUnitMapper {

  public BlacklistUnit mapActionCardToBlacklistUnit(String type, String id) {
    switch (type) {
      case "WALLET_ADS_OFFER":
        return new BlacklistUnit(BlacklistManager.BlacklistTypes.WALLET_ADS_OFFER.getType() + id,
            BlacklistManager.BlacklistTypes.WALLET_ADS_OFFER.getMaxPossibleImpressions());
      case "appc_card_info":
        return new BlacklistUnit(BlacklistManager.BlacklistTypes.WALLET_ADS_OFFER.getType() + id,
            BlacklistManager.BlacklistTypes.WALLET_ADS_OFFER.getMaxPossibleImpressions());
      default:
        throw new IllegalArgumentException(
            "Wrong blacklist key. Please, make sure you are passing the correct action card type and id.");
    }
  }
}
