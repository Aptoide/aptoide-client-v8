package cm.aptoide.pt.comment.data;

public class User {
  private final long id;
  private final String avatar;
  private final String name;

  public User(long id, String avatar, String name) {
    this.id = id;
    this.avatar = avatar;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getName() {
    return name;
  }
}
