package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.pt.v8engine.notification.policies.CampaignPolicy;
import cm.aptoide.pt.v8engine.notification.policies.DefaultPolicy;
import cm.aptoide.pt.v8engine.notification.policies.SocialPolicy;

/**
 * Created by trinkes on 16/05/2017.
 */

public class NotificationPolicyFactory {

  private NotificationProvider notificationProvider;

  public NotificationPolicyFactory(NotificationProvider notificationProvider) {
    this.notificationProvider = notificationProvider;
  }

  Policy getPolicy(AptoideNotification notification) {
    switch (notification.getType()) {
      case AptoideNotification.CAMPAIGN:
        return new CampaignPolicy();
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return new SocialPolicy(notificationProvider,
            new Integer[] { AptoideNotification.COMMENT, AptoideNotification.LIKE });
      case AptoideNotification.POPULAR:
        return new SocialPolicy(notificationProvider,
            new Integer[] { AptoideNotification.POPULAR });
      default:
        return new DefaultPolicy();
    }
  }
}
