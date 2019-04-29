package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class LocalNotificationSync extends Sync {
  public static final String APPC_CAMPAIGN_NOTIFICATION = "APPC_CAMPAIGN";
  private static final long TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000;
  private final NotificationProvider notificationProvider;
  private final String title;
  private final String body;
  private final String image;
  private final String navigationUrl;
  private final long trigger;
  private final String id;

  public LocalNotificationSync(NotificationProvider notificationProvider, boolean periodic,
      boolean exact, long interval, long trigger, String title, String body, String image,
      String navigationUrl, String id) {
    super(APPC_CAMPAIGN_NOTIFICATION, periodic, exact, trigger, interval);
    this.notificationProvider = notificationProvider;
    this.title = title;
    this.body = body;
    this.image = image;
    this.navigationUrl = navigationUrl;
    this.trigger = trigger;
    this.id = id;
  }

  private AptoideNotification createNotification() {
    return new AptoideNotification(body, image, title, navigationUrl,
        AptoideNotification.APPC_PROMOTION, System.currentTimeMillis(), "", "",
        AptoideNotification.NOT_DISMISSED, id, "", "", false,
        System.currentTimeMillis() + TWENTY_FOUR_HOURS);
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
}
