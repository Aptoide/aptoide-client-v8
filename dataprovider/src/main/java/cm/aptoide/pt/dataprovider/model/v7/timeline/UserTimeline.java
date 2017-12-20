package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;

public class UserTimeline {

  private String name;
  private String avatar;
  private Store store;

  public UserTimeline() {
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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $avatar = this.getAvatar();
    result = result * PRIME + ($avatar == null ? 43 : $avatar.hashCode());
    final Object $store = this.getStore();
    result = result * PRIME + ($store == null ? 43 : $store.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof UserTimeline)) return false;
    final UserTimeline other = (UserTimeline) o;
    if (!other.canEqual(this)) return false;
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
    return this$store == null ? other$store == null : this$store.equals(other$store);
  }

  public String toString() {
    return "UserTimeline(name="
        + this.getName()
        + ", avatar="
        + this.getAvatar()
        + ", store="
        + this.getStore()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof UserTimeline;
  }
}
