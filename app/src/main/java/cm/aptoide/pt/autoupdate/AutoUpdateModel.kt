package cm.aptoide.pt.autoupdate


data class AutoUpdateModel(val versionCode: Int, val uri: String, val md5: String,
                           val minSdk: String, val packageName: String,
                           val shouldUpdate: Boolean = false, var error: Error? = null,
                           var loading: Boolean = false) {

  constructor(error: Error?) : this(-1, "", "", "", "", error = error)

  constructor(loading: Boolean) : this(-1, "", "", "", "", loading = loading)

  fun hasError(): Boolean = error != null
}

enum class Error {
  NETWORK, GENERIC
}