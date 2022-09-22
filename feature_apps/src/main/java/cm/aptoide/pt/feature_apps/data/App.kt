package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store

data class App(
  val name: String,
  val packageName: String,
  val md5: String,
  val appSize: Long,
  val icon: String,
  val malware: String?,
  val rating: Rating,
  val downloads: Int,
  val versionName: String,
  val versionCode: Int,
  val featureGraphic: String,
  val isAppCoins: Boolean,
  val screenshots: List<String>?,
  val description: String?,
  val store: Store,
  val releaseDate: String?,
  val updateDate: String?,
  val website: String?,
  val email: String?,
  val privacyPolicy: String?,
  val permissions: List<String>?, val file: File, val obb: Obb?
)

data class File(
  var vername: String,
  var vercode: Int,
  var md5: String,
  var filesize: Long
)

data class Obb(val main: File, val patch: File?)
