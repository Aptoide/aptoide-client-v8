package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.V8Engine;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserManager {

  private V8Engine v8Engine;
  private SpotAndShareUserPersister persister;

  public SpotAndShareUserManager(V8Engine v8Engine, SpotAndShareUserPersister persister) {
    this.v8Engine = v8Engine;
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
    v8Engine.updateFriendProfileOnSpotAndShare();
  }
}
