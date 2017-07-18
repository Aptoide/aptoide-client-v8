package cm.aptoide.pt.v8engine.social.data.publisher;

import cm.aptoide.pt.preferences.Application;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class AptoidePublisher implements Publisher {
  @Override public String getPublisherName() {
    return Application.getConfiguration()
        .getMarketName();
  }

  @Override public PublisherAvatar getPublisherAvatar() {
    return new PublisherAvatar(Application.getConfiguration()
        .getIcon());
  }
}
