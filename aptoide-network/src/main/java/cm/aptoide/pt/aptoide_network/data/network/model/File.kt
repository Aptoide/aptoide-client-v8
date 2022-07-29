package cm.aptoide.pt.aptoide_network.data.network.model

data class File(
  val vername: String,
  val vercode: Int,
  val md5sum: String,
  val filesize: Long,
  val added: String?,
  val path: String,
  val path_alt: String,
  val signature: Signature?,
  val malware: Malware?,
  val used_features: List<String>?,
  val used_permissions: List<String>?,
)

data class Screenshot(var url: String, var height: Int, var width: Int)

data class Signature(
  var sha1: String,
  var owner: String

)
