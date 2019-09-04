package cm.aptoide.pt.app.view.googleplayservices

import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.dataprovider.model.v7.Obb

data class PlayServicesApp(var downloadModel: DownloadModel? = null,
                           var isInstalled: Boolean = false,
                           val appName: String = "",
                           val icon: String = "",
                           val id: Long = -2,
                           val packageName: String,
                           val versionCode: Long = -1,
                           val versionName: String? = null,
                           val md5sum: String? = null,
                           val path: String? = null,
                           val pathAlt: String = "",
                           val obb: Obb? = null,
                           val size: Long = 0,
                           val developer: String = "")