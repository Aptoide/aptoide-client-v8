package cm.aptoide.pt.v8engine.notification;

import java.util.List;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public interface NotificationNetworkService {
  Single<List<AptoideNotification>> getSocialNotifications();

  Single<List<AptoideNotification>> getCampaignNotifications();
}
