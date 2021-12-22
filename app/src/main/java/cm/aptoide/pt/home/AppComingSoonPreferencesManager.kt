package cm.aptoide.pt.home

import cm.aptoide.pt.appview.PreferencesPersister

class AppComingSoonPreferencesManager(val preferencesPersister: PreferencesPersister) {

  fun registerUserNotification(packageName: String) {
    return preferencesPersister.save(packageName, true)
  }

  fun isNotificationScheduled(packageName: String): Boolean {
    return preferencesPersister.get(packageName, false)
  }

  fun cancelScheduledNotification(packageName: String) {
    return preferencesPersister.save(packageName, false)
  }

}