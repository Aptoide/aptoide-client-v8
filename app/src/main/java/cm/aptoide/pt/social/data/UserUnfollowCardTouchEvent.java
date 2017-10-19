package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 19/10/2017.
 */

public class UserUnfollowCardTouchEvent extends CardTouchEvent {
  private final String name;

  public UserUnfollowCardTouchEvent(String name, int position, Post post) {
    super(post, position, Type.UNFOLLOW_USER);
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
