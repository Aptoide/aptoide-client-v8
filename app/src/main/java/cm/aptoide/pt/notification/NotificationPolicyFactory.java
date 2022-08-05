package cm.aptoide.pt.notification;

import cm.aptoide.pt.install.InstalledAppsRepository;
import cm.aptoide.pt.notification.policies.AlwaysShowPolicy;
import cm.aptoide.pt.notification.policies.CampaignPolicy;
import cm.aptoide.pt.notification.policies.DefaultPolicy;
import cm.aptoide.pt.notification.policies.SocialPolicy;

/**
 * Created by trinkes on 16/05/2017.
 */

public class NotificationPolicyFactory {

  private final NotificationProvider notificationProvider;
  private final InstalledAppsRepository installedAppsRepository;

  public NotificationPolicyFactory(NotificationProvider notificationProvider,
      InstalledAppsRepository installedAppsRepository) {
    this.notificationProvider = notificationProvider;
    this.installedAppsRepository = installedAppsRepository;
  }

  Policy getPolicy(AptoideNotification notification) {
    switch (notification.getType()) {
      case AptoideNotification.APPS_READY_TO_INSTALL:
      case AptoideNotification.NEW_FEATURE:
      case AptoideNotification.APPC_PROMOTION:
        return new AlwaysShowPolicy();
      case AptoideNotification.CAMPAIGN:
        return new CampaignPolicy(notification.getWhitelistedPackages(), installedAppsRepository);
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
      case AptoideNotification.NEW_ACTIVITY:
      case AptoideNotification.NEW_SHARE:
      case AptoideNotification.NEW_FOLLOWER:
        return new SocialPolicy(notificationProvider, new Integer[] {
            AptoideNotification.COMMENT, AptoideNotification.LIKE, AptoideNotification.NEW_SHARE,
            AptoideNotification.NEW_ACTIVITY, AptoideNotification.NEW_FOLLOWER
        });
      case AptoideNotification.POPULAR:
        return new SocialPolicy(notificationProvider,
            new Integer[] { AptoideNotification.POPULAR });
      default:
        return new DefaultPolicy();
    }
  }
}
