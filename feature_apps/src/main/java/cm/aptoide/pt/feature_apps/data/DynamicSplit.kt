package cm.aptoide.pt.feature_apps.data

data class DynamicSplit(
  val type: String,
  val file: File,
  val deliveryTypes: List<String>,
  val splits: List<File>,
)
