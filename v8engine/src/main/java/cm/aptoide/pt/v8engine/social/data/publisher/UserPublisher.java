package cm.aptoide.pt.v8engine.social.data.publisher;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class UserPublisher implements Publisher {
  private final String userName;
  private final String userAvatar;
  private final String storeName;
  private final String storeAvatar;

  public UserPublisher(String userName, String userAvatar, String storeName, String storeAvatar) {
    this.userName = userName;
    this.userAvatar = userAvatar;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
  }

  @Override public String getPublisherName() {
    return this.userName;
  }

  @Override public String getPublisherAvatar() {
    return this.userAvatar;
  }
}
