package cm.aptoide.pt.installedapps.data

import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import cm.aptoide.pt.installedapps.domain.model.InstalledAppState

class InstalledAppStateMapper {

  fun mapInstalledAppState(installedState: InstalledState): InstalledAppState {
    return when (installedState) {
      InstalledState.INSTALLED -> {
        InstalledAppState.INSTALLED
      }
      InstalledState.INSTALLING -> {
        InstalledAppState.INSTALLING
      }
      InstalledState.NOT_INSTALLED -> {
        InstalledAppState.NOT_INSTALLED
      }
      InstalledState.DOWNLOADING -> {
        InstalledAppState.DOWNLOADING
      }
    }
  }
}