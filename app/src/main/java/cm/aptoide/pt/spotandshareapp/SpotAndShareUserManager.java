package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.AptoideApplication;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserManager {

  private AptoideApplication aptoideApplication;
  private SpotAndShareUserPersister persister;

  public SpotAndShareUserManager(AptoideApplication aptoideApplication,
      SpotAndShareUserPersister persister) {
    this.aptoideApplication = aptoideApplication;
    this.persister = persister;
  }

  public void createUser(SpotAndShareUser user) {
    persister.save(user);
    updateFriendOnSpotAndShare();
  }

  public SpotAndShareUser getUser() {
    return persister.get();
  }

  public void updateUser(SpotAndShareUser user) {
    persister.save(user);
    updateFriendOnSpotAndShare();
  }

  private void updateFriendOnSpotAndShare() {
    aptoideApplication.updateFriendProfileOnSpotAndShare();
  }
}
