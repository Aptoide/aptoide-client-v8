package cm.aptoide.pt.installedapps.data

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity

class LocalInstalledAppsProvider(val packageManager: PackageManager) : InstalledAppsProvider {


  override fun getInstalledApps(): ArrayList<InstalledAppEntity> {
    val installedAppsList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    installedAppsList.sortWith { firstApp: PackageInfo, secondApp: PackageInfo -> ((firstApp.firstInstallTime - secondApp.firstInstallTime) / 1000).toInt() }
    return mapInstalledAppsList(installedAppsList)
  }

  private fun mapInstalledAppsList(installedAppsList: List<PackageInfo>): ArrayList<InstalledAppEntity> {
    val installedList = ArrayList<InstalledAppEntity>()
    for (packageInfo in installedAppsList) {
      installedList.add(
        InstalledAppEntity(
          packageInfo.applicationInfo.loadLabel(packageManager).toString(),
          packageInfo.packageName,
          packageInfo.versionName,
          "android.resource://" + packageInfo.packageName + "/" + packageInfo.applicationInfo.icon
        )
      )
    }
    return installedList
  }

}