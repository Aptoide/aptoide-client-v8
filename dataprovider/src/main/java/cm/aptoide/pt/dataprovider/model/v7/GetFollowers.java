package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;

/**
 * Created by trinkes on 16/12/2016.
 */
public class GetFollowers extends BaseV7EndlessDataListResponse<GetFollowers.TimelineUser> {

  public GetFollowers() {
  }

  public String toString() {
    return "GetFollowers()";
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetFollowers)) return false;
    final GetFollowers other = (GetFollowers) o;
    if (!other.canEqual((Object) this)) return false;
    return true;
  }

  public int hashCode() {
    int result = 1;
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetFollowers;
  }

  public static class TimelineUser {
    long id;
    String name;
    String avatar;
    Store store;
    TimelineStats.StatusData stats;

    public TimelineUser() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAvatar() {
      return this.avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public Store getStore() {
      return this.store;
    }

    public void setStore(Store store) {
      this.store = store;
    }

    public TimelineStats.StatusData getStats() {
      return this.stats;
    }

    public void setStats(TimelineStats.StatusData stats) {
      this.stats = stats;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $avatar = this.getAvatar();
      result = result * PRIME + ($avatar == null ? 43 : $avatar.hashCode());
      final Object $store = this.getStore();
      result = result * PRIME + ($store == null ? 43 : $store.hashCode());
      final Object $stats = this.getStats();
      result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof TimelineUser)) return false;
      final TimelineUser other = (TimelineUser) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$avatar = this.getAvatar();
      final Object other$avatar = other.getAvatar();
      if (this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar)) {
        return false;
      }
      final Object this$store = this.getStore();
      final Object other$store = other.getStore();
      if (this$store == null ? other$store != null : !this$store.equals(other$store)) return false;
      final Object this$stats = this.getStats();
      final Object other$stats = other.getStats();
      if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
      return true;
    }

    public String toString() {
      return "GetFollowers.TimelineUser(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", avatar="
          + this.getAvatar()
          + ", store="
          + this.getStore()
          + ", stats="
          + this.getStats()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof TimelineUser;
    }
  }
}

//"list": {
//    "id": 2552022,
//    "name": "Aptoide Agent",
//    "avatar": "http://pool.img.aptoide.com/user/228f730fc48999593475e0ab7ce0ad6f_avatar.png",
//    "store": {
//      "id": 798468,
//      "name": "rmota",
//      "avatar": "http://pool.img.aptoide.com/rmota/a346ea94af55291088a6e2d8da2e9280_ravatar.png"
//    },
//    "stats": {
//      "followers": 92,
//      "following": 376
//    }
//}

