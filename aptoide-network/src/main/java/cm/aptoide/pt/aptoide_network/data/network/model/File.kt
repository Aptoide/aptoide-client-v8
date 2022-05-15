package cm.aptoide.pt.aptoide_network.data.network.model

data class File(
  var vername: String,
  var vercode: Long,
  var md5Sum: String,
  var filesize: Long,
  var added: String?,
  var path: String?,
  var path_alt: String?,
  var signature: Signature?,
  val malware: Malware?,
  val used_features: List<String>?,
  val used_permissions: List<String>?,
)

data class Screenshot(var url: String, var height: Int, var width: Int)

data class Signature(
  var sha1: String,
  var owner: String

)
