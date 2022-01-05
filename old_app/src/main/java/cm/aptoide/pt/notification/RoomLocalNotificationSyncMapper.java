package cm.aptoide.pt.notification;

import cm.aptoide.pt.database.room.RoomLocalNotificationSync;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;

public class RoomLocalNotificationSyncMapper {

  public RoomLocalNotificationSyncMapper() {
  }

  public RoomLocalNotificationSync map(LocalNotificationSync sync) {
    return new RoomLocalNotificationSync(sync.getId(), sync.getTitle(), sync.getBody(),
        sync.getImage(), sync.getActionString(), sync.getNavigationUrl(), sync.getTrigger(),
        sync.getId(), sync.getType());
  }

  public LocalNotificationSync map(RoomLocalNotificationSync sync, NotificationProvider provider) {
    return new LocalNotificationSync(provider, false, false, 0, sync.getTrigger(), sync.getTitle(),
        sync.getBody(), sync.getImage(), sync.getActionStringRes(), sync.getNavigationUrl(),
        sync.getId(), sync.getType());
  }
}
