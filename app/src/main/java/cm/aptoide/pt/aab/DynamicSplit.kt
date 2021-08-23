package cm.aptoide.pt.aab

data class DynamicSplit(
    val name: String, val type: String, val md5Sum: String,
    val path: String, val fileSize: Long, val deliveryTypes: List<String>,
    val configSplits: List<Split>)
