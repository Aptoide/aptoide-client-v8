package cm.aptoide.pt.extensions

fun Float.format(decimals: Int? = null): String = toDouble().format(decimals)
