package cm.aptoide.pt.blacklist;

public class BlacklistManager {

  private BlacklistUnitMapper blacklistUnitMapper;
  private Blacklister blacklister;

  public BlacklistManager(Blacklister blacklister, BlacklistUnitMapper blacklistUnitMapper) {
    this.blacklister = blacklister;
    this.blacklistUnitMapper = blacklistUnitMapper;
  }

  public boolean isBlacklisted(String blacklistId) {
    return blacklister.isBlacklisted(blacklistUnitMapper.mapToBlacklistUnit(blacklistId));
  }

  public void addImpression(String blacklistId) {
    blacklister.addImpression(blacklistUnitMapper.mapToBlacklistUnit(blacklistId));
  }

  public void blacklist(String blacklistId) {
    blacklister.blacklist(blacklistUnitMapper.mapToBlacklistUnit(blacklistId));
  }

  public enum BlacklistUnit {
    WALLET_ADS_OFFER("Wallet_Ads_Offer", 10), APPC_CARD_INFO("Appc_Card_Info", 10);

    private String id;
    private int maxPossibleImpressions;

    BlacklistUnit(String id, int maxPossibleImpressions) {
      this.id = id;
      this.maxPossibleImpressions = maxPossibleImpressions;
    }

    public String getId() {
      return id;
    }

    public int getMaxPossibleImpressions() {
      return maxPossibleImpressions;
    }
  }
}
