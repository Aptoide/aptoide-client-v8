package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.feature_apps.domain.AppSource

data class PaEApp(
  override val packageName: String,
  val icon: String,
  val graphic: String,
  val name: String,
  val uname: String,
  val progress: PaEProgress?
) : AppSource

data class PaEProgress(
  val current: Int?,
  val target: Int,
  val type: String,
  val status: String?
)
