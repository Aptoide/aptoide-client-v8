package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 19/10/2017.
 */

public class UserUnfollowCardTouchEvent extends CardTouchEvent {
  private final String name;
  private final Long id;

  public UserUnfollowCardTouchEvent(Long id, String name, int position, Post post) {
    super(post, position, Type.UNFOLLOW_USER);
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Long getId() {
    return id;
  }
}
