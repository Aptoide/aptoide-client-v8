package cm.aptoide.pt.database

import cm.aptoide.pt.database.room.RoomInstallation
import cm.aptoide.pt.database.room.RoomInstalled
import java.util.*

class RoomInstallationMapper {
  fun map(installedList: List<RoomInstalled>): List<RoomInstallation> {

    val installationList: MutableList<RoomInstallation> =
        ArrayList()
    for (installed in installedList) {
      installationList.add(
          RoomInstallation(installed.packageName, installed.name, installed.icon,
              installed.versionCode, installed.versionName))
    }
    return installationList
  }

  fun map(installed: RoomInstalled): RoomInstallation {
    return RoomInstallation(installed.packageName, installed.name,
        installed.icon, installed.versionCode, installed.versionName)
  }
}