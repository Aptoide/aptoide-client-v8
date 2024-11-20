package cm.aptoide.pt.extensions

fun Double.format(decimals: Int? = null): String = if (this == toLong().toDouble()) {
  "%d".format(toLong())
} else {
  (decimals?.let { "%.${it}f" } ?: "%f").format(this)
}
