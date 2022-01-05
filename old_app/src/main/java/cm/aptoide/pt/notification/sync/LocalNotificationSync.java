package cm.aptoide.pt.notification.sync;

import androidx.annotation.StringRes;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class LocalNotificationSync extends Sync {
  public static final String APPC_CAMPAIGN_NOTIFICATION = "APPC_CAMPAIGN";
  public static final String NEW_FEATURE = "NEW_FEATURE";
  private static final long TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000;
  private final NotificationProvider notificationProvider;
  private final String title;
  private final String body;
  private final String image;
  private final String navigationUrl;
  private final long trigger;
  private final String id;
  private final int actionString;
  private final int type;

  public LocalNotificationSync(NotificationProvider notificationProvider, boolean periodic,
      boolean exact, long interval, long trigger, String title, String body, String image,
      @StringRes int actionString, String navigationUrl, String id, int type) {
    super(id, periodic, exact, trigger, interval);
    this.notificationProvider = notificationProvider;
    this.title = title;
    this.body = body;
    this.image = image;
    this.navigationUrl = navigationUrl;
    this.trigger = trigger;
    this.id = id;
    this.actionString = actionString;
    this.type = type;
  }

  private AptoideNotification createNotification() {
    return new AptoideNotification(body, image, title, navigationUrl, type,
        System.currentTimeMillis(), "", "", AptoideNotification.NOT_DISMISSED, id, "", "", false,
        System.currentTimeMillis() + TWENTY_FOUR_HOURS, actionString);
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getImage() {
    return image;
  }

  public String getNavigationUrl() {
    return navigationUrl;
  }

  public String getId() {
    return id;
  }

  public long getTrigger() {
    return trigger;
  }

  @Override public Completable execute() {
    return notificationProvider.save(createNotification());
  }

  public int getActionString() {
    return actionString;
  }

  public int getType() {
    return type;
  }
}
