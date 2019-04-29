package cm.aptoide.pt.autoupdate


data class AutoUpdateModel(val versionCode: Int, val uri: String, val md5: String,
                           val minSdk: String, val packageName: String,
                           val shouldUpdate: Boolean = false, var status: Status = Status.SUCCESS,
                           var loading: Boolean = false) {

  constructor(status: Status) : this(-1, "", "", "", "", status = status)

  constructor(loading: Boolean) : this(-1, "", "", "", "", loading = loading)

  fun wasSuccess(): Boolean = status == Status.SUCCESS
}

enum class Status {
  ERROR_NETWORK, ERROR_GENERIC, SUCCESS
}