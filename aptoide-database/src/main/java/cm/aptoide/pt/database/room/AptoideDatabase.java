package cm.aptoide.pt.database.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import cm.aptoide.pt.database.BuildConfig;

import static cm.aptoide.pt.database.room.AptoideDatabase.VERSION;

/**
 * Database holder that contains the list of entities (tables) associated with the database.
 */
@Database(entities = {
    RoomEvent.class, RoomExperiment.class, RoomStoredMinimalAd.class, RoomNotification.class,
    RoomLocalNotificationSync.class, RoomInstalled.class, RoomInstallation.class,
    RoomMigratedApp.class, RoomUpdate.class, RoomDownload.class, RoomStore.class,
    RoomAptoideInstallApp.class, RoomAppComingSoonRegistration.class
}, version = VERSION) @TypeConverters({
    SplitTypeConverter.class, StringTypeConverter.class, FileToDownloadTypeConverter.class,
    CampaignUrlTypeConverter.class
}) public abstract class AptoideDatabase extends RoomDatabase {

  /**
   * Database Schema version
   */
  static final int VERSION = BuildConfig.ROOM_SCHEMA_VERSION;

  public abstract EventDAO eventDAO();

  public abstract ExperimentDAO experimentDAO();

  public abstract StoredMinimalAdDAO storeMinimalAdDAO();

  public abstract NotificationDao notificationDao();

  public abstract LocalNotificationSyncDao localNotificationSyncDao();

  public abstract InstalledDao installedDao();

  public abstract InstallationDao installationDao();

  public abstract MigratedAppDAO migratedAppDAO();

  public abstract UpdateDao updateDao();

  public abstract DownloadDAO downloadDAO();

  public abstract StoreDao storeDao();

  public abstract AptoideInstallDao aptoideInstallDao();

  public abstract AppComingSoonRegistrationDAO appComingSoonRegistrationDAO();
}
