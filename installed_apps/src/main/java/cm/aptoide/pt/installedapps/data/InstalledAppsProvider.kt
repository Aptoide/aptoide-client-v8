package cm.aptoide.pt.installedapps.data

import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity

interface InstalledAppsProvider {

  fun getInstalledApps(): ArrayList<InstalledAppEntity>
}