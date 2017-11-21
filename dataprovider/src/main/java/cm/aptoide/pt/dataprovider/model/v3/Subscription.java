/*
 * Copyright (c) 2016.
 * Modified on 27/06/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

/**
 * Created on 27/06/16.
 */
public class Subscription {

  private Number id;
  private String name;
  private String avatar;
  private String downloads;
  private String theme;
  private String description;
  private String items;
  private String view;
  private String avatarHd;

  public Subscription() {
  }

  public Number getId() {
    return this.id;
  }

  public void setId(Number id) {
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

  public String getDownloads() {
    return this.downloads;
  }

  public void setDownloads(String downloads) {
    this.downloads = downloads;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getItems() {
    return this.items;
  }

  public void setItems(String items) {
    this.items = items;
  }

  public String getView() {
    return this.view;
  }

  public void setView(String view) {
    this.view = view;
  }

  public String getAvatarHd() {
    return this.avatarHd;
  }

  public void setAvatarHd(String avatarHd) {
    this.avatarHd = avatarHd;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $avatar = this.getAvatar();
    result = result * PRIME + ($avatar == null ? 43 : $avatar.hashCode());
    final Object $downloads = this.getDownloads();
    result = result * PRIME + ($downloads == null ? 43 : $downloads.hashCode());
    final Object $theme = this.getTheme();
    result = result * PRIME + ($theme == null ? 43 : $theme.hashCode());
    final Object $description = this.getDescription();
    result = result * PRIME + ($description == null ? 43 : $description.hashCode());
    final Object $items = this.getItems();
    result = result * PRIME + ($items == null ? 43 : $items.hashCode());
    final Object $view = this.getView();
    result = result * PRIME + ($view == null ? 43 : $view.hashCode());
    final Object $avatarHd = this.getAvatarHd();
    result = result * PRIME + ($avatarHd == null ? 43 : $avatarHd.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Subscription)) return false;
    final Subscription other = (Subscription) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$avatar = this.getAvatar();
    final Object other$avatar = other.getAvatar();
    if (this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar)) {
      return false;
    }
    final Object this$downloads = this.getDownloads();
    final Object other$downloads = other.getDownloads();
    if (this$downloads == null ? other$downloads != null
        : !this$downloads.equals(other$downloads)) {
      return false;
    }
    final Object this$theme = this.getTheme();
    final Object other$theme = other.getTheme();
    if (this$theme == null ? other$theme != null : !this$theme.equals(other$theme)) return false;
    final Object this$description = this.getDescription();
    final Object other$description = other.getDescription();
    if (this$description == null ? other$description != null
        : !this$description.equals(other$description)) {
      return false;
    }
    final Object this$items = this.getItems();
    final Object other$items = other.getItems();
    if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
    final Object this$view = this.getView();
    final Object other$view = other.getView();
    if (this$view == null ? other$view != null : !this$view.equals(other$view)) return false;
    final Object this$avatarHd = this.getAvatarHd();
    final Object other$avatarHd = other.getAvatarHd();
    if (this$avatarHd == null ? other$avatarHd != null : !this$avatarHd.equals(other$avatarHd)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "Subscription(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", avatar="
        + this.getAvatar()
        + ", downloads="
        + this.getDownloads()
        + ", theme="
        + this.getTheme()
        + ", description="
        + this.getDescription()
        + ", items="
        + this.getItems()
        + ", view="
        + this.getView()
        + ", avatarHd="
        + this.getAvatarHd()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof Subscription;
  }
}
