package cm.aptoide.pt.model.v7.timeline;

public class UserSharerTimeline {
  private User user;
  private Store store;

  public UserSharerTimeline() {
  }

  public UserSharerTimeline(User user, Store store) {
    this.user = user;
    this.store = store;
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

  public class User {
    private String name;
    private String avatar;

    public User() {

    }

    public User(String name, String avatar) {
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
  }

  public class Store {
    private String name;
    private String avatar;

    public Store() {

    }

    public Store(String name, String avatar) {
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
  }
}
