package cm.aptoide.pt.extensions

import kotlin.math.pow

fun Long.getSizeString(): String {
  return if (this < 1073741824L) {
    String.format("%.2f MB", toMb())
  } else {
    String.format("%.2f GB", toGb())
  }
}

fun Long.toMb(): Double {
  return (this / 1024.0.pow(2.0))
}

fun Long.toGb(): Double {
  return (this / 1024.0.pow(3.0))
}
