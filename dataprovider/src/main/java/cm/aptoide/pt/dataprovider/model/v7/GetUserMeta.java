package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;

/**
 * Created by pedroribeiro on 29/05/17.
 */

public class GetUserMeta extends BaseV7Response {

  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private long id;
    private String name;
    private int level;
    private String avatar;
    private String added;
    private String modified;
    private Identity identity;
    private Store store;
    private String access;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getLevel() {
      return level;
    }

    public void setLevel(int level) {
      this.level = level;
    }

    public String getAvatar() {
      return avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public String getAdded() {
      return added;
    }

    public void setAdded(String added) {
      this.added = added;
    }

    public String getModified() {
      return modified;
    }

    public void setModified(String modified) {
      this.modified = modified;
    }

    public Identity getIdentity() {
      return identity;
    }

    public void setIdentity(Identity identity) {
      this.identity = identity;
    }

    public Store getStore() {
      return store;
    }

    public void setStore(Store store) {
      this.store = store;
    }

    public String getAccess() {
      return access;
    }

    public void setAccess(String access) {
      this.access = access;
    }
  }

  public static class Identity {
    private String username;
    private String email;
    private String phone;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPhone() {
      return phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }
  }
}
