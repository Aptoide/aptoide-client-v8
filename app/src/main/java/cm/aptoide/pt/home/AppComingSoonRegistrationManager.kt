package cm.aptoide.pt.home

import cm.aptoide.pt.database.room.RoomAppComingSoonRegistration
import rx.Completable
import rx.Observable

class AppComingSoonRegistrationManager(
    private val appComingSoonRegistrationPersistence: AppComingSoonRegistrationPersistence) {

  fun registerUserNotification(packageName: String): Completable {
    return appComingSoonRegistrationPersistence.registerForAppNotification(
        RoomAppComingSoonRegistration(packageName))
  }

  fun isNotificationScheduled(packageName: String): Observable<Boolean> {
    return appComingSoonRegistrationPersistence.isRegisteredForApp(packageName)

  }

  fun cancelScheduledNotification(packageName: String): Completable {
    return appComingSoonRegistrationPersistence.unregisterForAppNotification(
        RoomAppComingSoonRegistration(packageName))
  }

}