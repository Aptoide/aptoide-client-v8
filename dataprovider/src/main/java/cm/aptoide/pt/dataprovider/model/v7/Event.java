/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by neuro on 10-05-2016.
 */
public class Event {

  private Type type; // API, v3
  private Name name; // listApps, getStore, getStoreWidgets, getApkComments
  private String action;
  private GetStoreWidgets.WSWidget.Data data;

  public Event() {
  }

  public Type getType() {
    return this.type;
  }

  public Event setType(Type type) {
    this.type = type;
    return this;
  }

  public Name getName() {
    return this.name;
  }

  public Event setName(Name name) {
    this.name = name;
    return this;
  }

  public String getAction() {
    return this.action;
  }

  public Event setAction(String action) {
    this.action = action;
    return this;
  }

  public GetStoreWidgets.WSWidget.Data getData() {
    return this.data;
  }

  public Event setData(GetStoreWidgets.WSWidget.Data data) {
    this.data = data;
    return this;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $type = this.getType();
    result = result * PRIME + ($type == null ? 43 : $type.hashCode());
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $action = this.getAction();
    result = result * PRIME + ($action == null ? 43 : $action.hashCode());
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Event)) return false;
    final Event other = (Event) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$type = this.getType();
    final Object other$type = other.getType();
    if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$action = this.getAction();
    final Object other$action = other.getAction();
    if (this$action == null ? other$action != null : !this$action.equals(other$action)) {
      return false;
    }
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  public String toString() {
    return "Event(type="
        + this.getType()
        + ", name="
        + this.getName()
        + ", action="
        + this.getAction()
        + ", data="
        + this.getData()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof Event;
  }

  public enum Type {
    API, CLIENT, v3
  }

  public enum Name {
    // Api
    listApps, listStores, getUser, getStore, getStoreWidgets, //getReviews,
    //getApkComments,
    listReviews, listComments, getMyStoresSubscribed, getStoresRecommended,

    // Client
    myStores, myUpdates, myExcludedUpdates, getAds, myDownloads, getAppCoinsAds,

    // Displays
    facebook, twitch, twitter, youtube,

    // v3
    getReviews
  }
}
