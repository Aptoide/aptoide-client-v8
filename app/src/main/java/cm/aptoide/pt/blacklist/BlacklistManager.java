package cm.aptoide.pt.blacklist;

public class BlacklistManager {

  private BlacklistUnitMapper blacklistUnitMapper;
  private Blacklister blacklister;

  public BlacklistManager(Blacklister blacklister, BlacklistUnitMapper blacklistUnitMapper) {
    this.blacklister = blacklister;
    this.blacklistUnitMapper = blacklistUnitMapper;
  }

  public boolean isBlacklisted(String actionCardType, String id) {
    return blacklister.isBlacklisted(
        blacklistUnitMapper.mapActionCardToBlacklistUnit(actionCardType, id));
  }

  public void addImpression(String actionCardType, String id) {
    blacklister.addImpression(blacklistUnitMapper.mapActionCardToBlacklistUnit(actionCardType, id));
  }

  public void blacklist(String actionCardType, String id) {
    blacklister.blacklist(blacklistUnitMapper.mapActionCardToBlacklistUnit(actionCardType, id));
  }

  public enum BlacklistType {
    WALLET_ADS_OFFER("Wallet_Ads_Offer", 10), APPC_CARD_INFO("Appc_Card_Info", 10);

    private String type;
    private int maxPossibleImpressions;

    BlacklistType(String type, int maxPossibleImpressions) {
      this.type = type;
      this.maxPossibleImpressions = maxPossibleImpressions;
    }

    public String getType() {
      return type;
    }

    public int getMaxPossibleImpressions() {
      return maxPossibleImpressions;
    }
  }
}
