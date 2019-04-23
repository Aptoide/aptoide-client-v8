package cm.aptoide.pt.blacklist;

public class Blacklister {

  private BlacklistPersistence blacklistPersistence;

  public Blacklister(BlacklistPersistence blacklistPersistence) {
    this.blacklistPersistence = blacklistPersistence;
  }

  public boolean isBlacklisted(BlacklistUnit blacklistUnit) {
    return blacklistPersistence.isBlacklisted(blacklistUnit.getId(),
        blacklistUnit.getMaxImpressions());
  }

  public void addImpression(BlacklistUnit blacklistUnit) {
    blacklistPersistence.addImpression(blacklistUnit.getId(), blacklistUnit.getMaxImpressions());
  }

  public void blacklist(BlacklistUnit blacklistUnit) {
    blacklistPersistence.blacklist(blacklistUnit.getId());
  }
}
