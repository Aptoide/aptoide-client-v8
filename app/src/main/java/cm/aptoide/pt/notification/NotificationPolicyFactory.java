package cm.aptoide.pt.notification;

import cm.aptoide.pt.notification.policies.CampaignPolicy;
import cm.aptoide.pt.notification.policies.DefaultPolicy;
import cm.aptoide.pt.notification.policies.SocialPolicy;

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
      case AptoideNotification.NEW_ACTIVITY:
      case AptoideNotification.NEW_SHARE:
        return new SocialPolicy(notificationProvider, new Integer[] {
            AptoideNotification.COMMENT, AptoideNotification.LIKE, AptoideNotification.NEW_SHARE,
            AptoideNotification.NEW_ACTIVITY
        });
      case AptoideNotification.POPULAR:
        return new SocialPolicy(notificationProvider,
            new Integer[] { AptoideNotification.POPULAR });
      default:
        return new DefaultPolicy();
    }
  }
}
