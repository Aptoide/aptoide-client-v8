package cm.aptoide.pt.social.data.publisher;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class MediaPublisher implements Publisher {
  private final String mediaName;
  private final PublisherAvatar mediaAvatar;

  public MediaPublisher(String mediaName, PublisherAvatar mediaAvatar) {
    this.mediaName = mediaName;
    this.mediaAvatar = mediaAvatar;
  }

  @Override public String getPublisherName() {
    return this.mediaName;
  }

  @Override public PublisherAvatar getPublisherAvatar() {
    return this.mediaAvatar;
  }
}
