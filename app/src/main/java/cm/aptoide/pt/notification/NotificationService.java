package cm.aptoide.pt.notification;

import java.util.List;
import rx.Single;

public interface NotificationService {

  Single<List<AptoideNotification>> getSocialNotifications();

  Single<List<AptoideNotification>> getCampaignNotifications();
}
