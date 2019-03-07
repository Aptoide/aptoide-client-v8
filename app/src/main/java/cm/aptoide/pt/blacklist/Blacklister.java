package cm.aptoide.pt.blacklist;

public class Blacklister {

  private BlacklistPersistence blacklistPersistence;

  public Blacklister(BlacklistPersistence blacklistPersistence) {
    this.blacklistPersistence = blacklistPersistence;
  }

  public boolean isBlacklisted(BlacklistManager.BlacklistUnit blacklistUnit) {
    return blacklistPersistence.isBlacklisted(blacklistUnit.getId(),
        blacklistUnit.getMaxPossibleImpressions());
  }

  public void addImpression(BlacklistManager.BlacklistUnit blacklistUnit) {
    blacklistPersistence.addImpression(blacklistUnit.getId(),
        blacklistUnit.getMaxPossibleImpressions());
  }

  public void blacklist(BlacklistManager.BlacklistUnit blacklistUnit) {
    blacklistPersistence.blacklist(blacklistUnit.getId());
  }
}
