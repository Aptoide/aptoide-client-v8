package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetHomeMeta extends BaseV7Response {
  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private Store store;
    private HomeUser user;
    private Stats stats;

    public Store getStore() {
      return store;
    }

    public void setStore(Store store) {
      this.store = store;
    }

    public HomeUser getUser() {
      return user;
    }

    public void setUser(HomeUser user) {
      this.user = user;
    }

    public Stats getStats() {
      return stats;
    }

    public void setStats(Stats stats) {
      this.stats = stats;
    }
  }

  public static class Stats {
    private long followers;
    private long following;

    public long getFollowers() {
      return followers;
    }

    public void setFollowers(long followers) {
      this.followers = followers;
    }

    public long getFollowing() {
      return following;
    }

    public void setFollowing(long following) {
      this.following = following;
    }
  }
}
