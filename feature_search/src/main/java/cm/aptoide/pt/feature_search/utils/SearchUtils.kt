package cm.aptoide.pt.feature_search.utils

fun String.fixQuery() = replace("""[\r\n]""".toRegex(), "")

fun String.isValidSearch() =
  fixQuery().trim().let { it.isNotEmpty() && (it.length > 1) }
