package cm.aptoide.pt.home.apps

import cm.aptoide.pt.home.apps.model.AppcUpdateApp
import cm.aptoide.pt.home.apps.model.DownloadApp
import cm.aptoide.pt.home.apps.model.InstalledApp
import cm.aptoide.pt.home.apps.model.UpdateApp

/**
 * Describes the model state of AppsFragment. Should be immutable.
 */
data class AppsModel(val updates: List<UpdateApp>, val installed: List<InstalledApp>,
                     val migrations: List<AppcUpdateApp>,
                     val downloads: List<DownloadApp>)