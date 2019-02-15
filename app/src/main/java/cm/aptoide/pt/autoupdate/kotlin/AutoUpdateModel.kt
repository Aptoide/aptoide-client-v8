package cm.aptoide.pt.autoupdate.kotlin


data class AutoUpdateModel(val versionCode: Int = -1, val uri: String = "", val md5: String = "",
                           val minSdk: String = "", val packageName: String = "", val shouldUpdate: Boolean = false,
                           val error: Error? = null, val loading: Boolean = false) {

    fun hasError(): Boolean = error != null
}

enum class Error{
    NETWORK, GENERIC
}