package cm.aptoide.pt.social.data.publisher;

import android.support.annotation.DrawableRes;

public class AptoidePublisher implements Publisher {

  private final int drawableId;
  private final String publisherName;

  public AptoidePublisher(@DrawableRes int drawableId, String publisherName) {
    this.drawableId = drawableId;
    this.publisherName = publisherName;
  }

  @Override public String getPublisherName() {
    return publisherName;
  }

  @Override public PublisherAvatar getPublisherAvatar() {
    return new PublisherAvatar(drawableId);
  }
}
