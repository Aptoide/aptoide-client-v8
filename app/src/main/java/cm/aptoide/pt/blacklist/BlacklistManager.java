package cm.aptoide.pt.blacklist;

public class BlacklistManager {

  private BlacklistPersistence blacklistPersistence;

  public BlacklistManager(BlacklistPersistence blacklistPersistence) {
    this.blacklistPersistence = blacklistPersistence;
  }

  public boolean isBlacklisted(BlacklistUnit blacklistUnit) {
    return blacklistPersistence.isBlacklisted(blacklistUnit.getId(),
        blacklistUnit.getMaxPossibleImpressions());
  }

  public void addImpression(BlacklistUnit blacklistUnit) {
    blacklistPersistence.addImpression(blacklistUnit.getId(),
        blacklistUnit.getMaxPossibleImpressions());
  }

  public void blacklist(BlacklistUnit blacklistUnit) {
    blacklistPersistence.blacklist(blacklistUnit.getId(),
        blacklistUnit.getMaxPossibleImpressions());
  }

  public enum BlacklistUnit {
    WALLET_ADS_OFFER("Wallet_Ads_Offer", 10);

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
