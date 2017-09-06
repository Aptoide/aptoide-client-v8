package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.AptoideApplication;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareLocalUserManager {

  private AptoideApplication aptoideApplication;
  private SpotAndShareLocalUserPersister persister;

  public SpotAndShareLocalUserManager(AptoideApplication aptoideApplication,
      SpotAndShareLocalUserPersister persister) {
    this.aptoideApplication = aptoideApplication;
    this.persister = persister;
  }

  public void createUser(SpotAndShareLocalUser user) {
    persister.save(user);
    updateFriendOnSpotAndShare();
  }

  public SpotAndShareLocalUser getUser() {
    return persister.get();
  }

  public void updateUser(SpotAndShareLocalUser user) {
    persister.save(user);
    updateFriendOnSpotAndShare();
  }

  private void updateFriendOnSpotAndShare() {
    aptoideApplication.updateFriendProfileOnSpotAndShare();
  }
}
