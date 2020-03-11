package cm.aptoide.pt.database.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import cm.aptoide.pt.database.BuildConfig;

import static cm.aptoide.pt.database.room.AptoideDatabase.VERSION;

/**
 * Database holder that contains the list of entities (tables) associated with the database.
 */
@Database(entities = {
    RoomEvent.class, RoomExperiment.class, RoomStoredMinimalAd.class, RoomNotification.class,
    RoomLocalNotificationSync.class, RoomInstalled.class
}, version = VERSION) public abstract class AptoideDatabase extends RoomDatabase {

  /**
   * Database Schema version
   */
  static final int VERSION = BuildConfig.ROOM_SCHEMA_VERSION;

  public abstract EventDAO eventDAO();

  public abstract ExperimentDAO experimentDAO();

  public abstract StoredMinimalAdDAO storeMinimalAdDAO();

  public abstract NotificationDao notificationDao();

  public abstract LocalNotificationSyncDao localNotificationSyncDao();
}
