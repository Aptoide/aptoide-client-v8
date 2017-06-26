package cm.aptoide.pt.v8engine.social.data.publisher;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class MediaPublisher implements Publisher {
  private final String mediaName;
  private final String mediaAvatar;

  public MediaPublisher(String mediaName, String mediaAvatar) {
    this.mediaName = mediaName;
    this.mediaAvatar = mediaAvatar;
  }

  @Override public String getPublisherName() {
    return this.mediaName;
  }

  @Override public String getPublisherAvatar() {
    return this.mediaAvatar;
  }
}
