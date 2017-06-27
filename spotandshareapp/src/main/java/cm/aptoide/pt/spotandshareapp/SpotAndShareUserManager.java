package cm.aptoide.pt.spotandshareapp;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserManager {

  private SpotAndShareUserPersister persister;

  public SpotAndShareUserManager(SpotAndShareUserPersister persister) {
    this.persister = persister;
  }

  public void createUser(SpotAndShareUser user) {
    persister.save(user);
  }

  public SpotAndShareUser getUser() {
    return persister.get();
  }

  public void updateUser(SpotAndShareUser user) {
    persister.save(user);
  }
}
