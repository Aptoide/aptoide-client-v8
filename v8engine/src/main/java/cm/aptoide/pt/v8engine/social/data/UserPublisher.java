package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import cm.aptoide.pt.v8engine.social.data.publisher.PublisherAvatar;

/**
 * Created by jdandrade on 26/06/2017.
 */

class UserPublisher implements Publisher {
  @Override public String getPublisherName() {
    return "";
  }

  @Override public PublisherAvatar getPublisherAvatar() {
    return new PublisherAvatar("");
  }
}
