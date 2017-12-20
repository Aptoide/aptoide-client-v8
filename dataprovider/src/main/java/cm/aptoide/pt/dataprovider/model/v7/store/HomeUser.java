package cm.aptoide.pt.dataprovider.model.v7.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * Created by trinkes on 23/02/2017.
 */
public class HomeUser {
  private long id;
  private String name;
  private String avatar;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;

  public HomeUser() {
  }

  public long getId() {
    return this.id;
  }

  public HomeUser setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public HomeUser setName(String name) {
    this.name = name;
    return this;
  }

  public String getAvatar() {
    return this.avatar;
  }

  public HomeUser setAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  public Date getAdded() {
    return this.added;
  }

  public HomeUser setAdded(Date added) {
    this.added = added;
    return this;
  }

  public Date getModified() {
    return this.modified;
  }

  public HomeUser setModified(Date modified) {
    this.modified = modified;
    return this;
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
    final Object $added = this.getAdded();
    result = result * PRIME + ($added == null ? 43 : $added.hashCode());
    final Object $modified = this.getModified();
    result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof HomeUser)) return false;
    final HomeUser other = (HomeUser) o;
    if (!other.canEqual(this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$avatar = this.getAvatar();
    final Object other$avatar = other.getAvatar();
    if (this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar)) {
      return false;
    }
    final Object this$added = this.getAdded();
    final Object other$added = other.getAdded();
    if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
    final Object this$modified = this.getModified();
    final Object other$modified = other.getModified();
    return this$modified == null ? other$modified == null : this$modified.equals(other$modified);
  }

  public String toString() {
    return "HomeUser(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", avatar="
        + this.getAvatar()
        + ", added="
        + this.getAdded()
        + ", modified="
        + this.getModified()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof HomeUser;
  }
}
