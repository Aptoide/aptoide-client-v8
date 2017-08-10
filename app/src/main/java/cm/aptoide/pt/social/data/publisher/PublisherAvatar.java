package cm.aptoide.pt.social.data.publisher;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PublisherAvatar {

  private final String avatarUrl;
  private final int drawableAvatar;

  public PublisherAvatar(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    this.drawableAvatar = -1;
  }

  public PublisherAvatar(int drawableId) {
    this.drawableAvatar = drawableId;
    this.avatarUrl = null;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public int getDrawableId() {
    return drawableAvatar;
  }
}
