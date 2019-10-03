package cm.aptoide.pt.promotions

import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.dataprovider.model.v7.Obb

data class WalletApp(
    var downloadModel: DownloadModel? = null,
    var isInstalled: Boolean = false,
    val appName: String = "",
    val icon: String = "",
    val id: Long = -1,
    val packageName: String = "",
    val md5sum: String? = null,
    val versionCode: Int = -1,
    val versionName: String? = null,
    val path: String? = null,
    val pathAlt: String? = null,
    val obb: Obb? = null,
    val size: Long = 0,
    val developer: String = "",
    val rating: Float = -1f,
    val splits: List<Split> = emptyList(),
    val requiredSplits: List<String> = emptyList()

) {
  fun exists(): Boolean {
    return id != -1L
  }
}