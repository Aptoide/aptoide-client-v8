package cm.aptoide.pt.installer

import cm.aptoide.pt.feature_apps.data.File

data class DynamicSplit(
  val type: String,
  val file: File,
  val deliveryTypes: List<String>,
  val splits: List<File>,
)
