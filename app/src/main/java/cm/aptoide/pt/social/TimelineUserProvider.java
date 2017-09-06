package cm.aptoide.pt.social;

import cm.aptoide.pt.social.data.User;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 05/09/2017.
 */

public interface TimelineUserProvider {
  Observable<Boolean> isLoggedIn();

  Observable<TimelineNotification> getNotification();

  Completable notificationRead(NotificationType notificationType);

  Observable<User> getUser(Long userId);

  enum NotificationType {
    CAMPAIGN, COMMENT, LIKE, POPULAR
  }

  class TimelineNotification {
    private String body;
    private String img;
    private String url;
    private NotificationType type;

    public TimelineNotification(String body, String img, String url, NotificationType type) {
      this.body = body;
      this.img = img;
      this.url = url;
      this.type = type;
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

    public NotificationType getType() {
      return type;
    }
  }
}
