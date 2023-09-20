package cm.aptoide.pt.extensions

import kotlin.math.pow

fun Long.toMb(): Double {
  return (this / 1000.toDouble().pow(2.0))
}
