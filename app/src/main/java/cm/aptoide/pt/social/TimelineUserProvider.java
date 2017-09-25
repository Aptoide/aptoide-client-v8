package cm.aptoide.pt.social;

import cm.aptoide.pt.social.data.User;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 05/09/2017.
 */

public interface TimelineUserProvider {

  Completable notificationRead(int notificationId);

  Observable<User> getUser(boolean refresh);

  class TimelineNotification {
    private final String body;
    private final String img;
    private final String url;
    private final int notificationId;
    private final String analyticsUrl;

    public TimelineNotification(String body, String img, String url, int notificationId,
        String analyticsUrl) {
      this.body = body;
      this.img = img;
      this.url = url;
      this.notificationId = notificationId;
      this.analyticsUrl = analyticsUrl;
    }

    public String getBody() {
      return body;
    }

    public String getImg() {
      return img;
    }

    public String getUrl() {
      return url;
    }

    public int getNotificationId() {
      return notificationId;
    }

    public String getAnalyticsUrl() {
      return analyticsUrl;
    }
  }
}
