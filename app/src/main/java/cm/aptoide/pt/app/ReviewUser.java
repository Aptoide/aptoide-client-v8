package cm.aptoide.pt.app;

/**
 * Created by D01 on 22/05/2018.
 */

public class ReviewUser {
  private final long id;
  private final String avatar;
  private final String name;

  public ReviewUser(long id, String avatar, String name) {
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
