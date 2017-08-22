package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.Comment;

public class UserSharerTimeline {
  private User user;
  private Store store;

  public UserSharerTimeline() {
  }

  public UserSharerTimeline(User user, Store store) {
    this.user = user;
    this.store = store;
  }

  public UserSharerTimeline(Comment.User user,
      cm.aptoide.pt.dataprovider.model.v7.store.Store store) {
    this.user = new User(user.getId(), user.getName(), user.getAvatar());
    this.store = new Store(store.getName(), store.getAvatar(), store.getAppearance()
        .getTheme());
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public static class User {
    private Long id;
    private String name;
    private String avatar;

    public User() {

    }

    public User(long id, String name, String avatar) {
      this.id = id;
      this.name = name;
      this.avatar = avatar;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAvatar() {
      return avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public Long getId() {
      return id;
    }
  }

  public class Store {
    private String storeTheme;
    private String name;
    private String avatar;

    public Store() {

    }

    public Store(String name, String avatar, String storeTheme) {
      this.name = name;
      this.avatar = avatar;
      this.storeTheme = storeTheme;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAvatar() {
      return avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public String getStoreTheme() {
      return storeTheme;
    }
  }
}
