package cm.aptoide.pt.installedapps.data.fake

import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeInstalledAppsRepository : LocalInstalledAppsRepository {
  override fun getInstalledApps(): Flow<List<InstalledAppEntity>> {
    val fakeList = arrayListOf(
      InstalledAppEntity(
        "cm.aptoide.pt",
        "aptoide",
        "v10", 12,
        "https://cdn6.aptoide.com/imgs/a/4/a/a4a27be4bb6f17cd3fbb159dd7344146_icon.png",
        InstalledState.INSTALLED
      ),
      InstalledAppEntity(
        "com.sporting.app",
        "Sporting",
        "v23", 123,
        "https://icons.iconarchive.com/icons/giannis-zographos/portugese-football-club/256/Sporting-CP-Lisbon-icon.png",
        InstalledState.INSTALLED
      )
    )
    return flowOf(fakeList)
  }

  override fun addInstalledApp(installedAppEntity: InstalledAppEntity) {
    TODO("Not yet implemented")
  }

  override fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>) {
    TODO("Not yet implemented")
  }

  override fun removeInstalledApp(installedAppEntity: InstalledAppEntity) {
    TODO("Not yet implemented")
  }

  override fun getInstalledApp(versionCode: Int, packageName: String): Flow<InstalledAppEntity> {
    return flowOf(
      InstalledAppEntity(
        "com.sporting.app",
        "Sporting",
        "v23", 123,
        "https://icons.iconarchive.com/icons/giannis-zographos/portugese-football-club/256/Sporting-CP-Lisbon-icon.png",
        InstalledState.INSTALLED
      )
    )
  }

  override fun getInstalledAppsByType(installedState: InstalledState): Flow<List<InstalledAppEntity>> {
    val fakeList = arrayListOf(
      InstalledAppEntity(
        "cm.aptoide.pt",
        "aptoide",
        "v10", 12,
        "https://cdn6.aptoide.com/imgs/a/4/a/a4a27be4bb6f17cd3fbb159dd7344146_icon.png",
        installedState
      ),
      InstalledAppEntity(
        "com.sporting.app",
        "Sporting",
        "v23", 123,
        "https://icons.iconarchive.com/icons/giannis-zographos/portugese-football-club/256/Sporting-CP-Lisbon-icon.png",
        installedState
      )
    )
    return flowOf(fakeList)
  }
}