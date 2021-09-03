package cm.aptoide.pt.smart.appfiltering

data class AppToAdd(val json: String, val appType: String, val appCategory: String,
    val includeInTop: Boolean, val includeInLatest: Boolean)
