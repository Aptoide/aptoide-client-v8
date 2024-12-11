package cm.aptoide.pt.download

class DownloadSpeedIntervalMapper {

  fun getDownloadSpeedInterval(kilobytesPerSecond: Long): Pair<String, String> {
    val bytesPerSecond = kilobytesPerSecond * 1024
    var speed = bytesPerSecond.toDouble()
    var scale = "BPS"

    if (speed >= 1024) {
      speed /= 1024
      scale = "KBPS"
    }
    if (speed >= 1024) {
      speed /= 1024
      scale = "MBPS"
    }
    if (speed >= 1024) {
      speed /= 1024
      scale = "GBPS"
    }

    val interval = when {
      speed == 0.0 -> "0"
      speed <= 2 -> "1-2"
      speed <= 5 -> "3-5"
      speed <= 10 -> "6-10"
      speed <= 20 -> "11-20"
      speed <= 50 -> "21-50"
      speed <= 100 -> "51-100"
      speed <= 200 -> "101-200"
      speed <= 500 -> "201-500"
      speed <= 1000 -> "501-1000"
      else -> ">1000"
    }

    return Pair(interval, scale)
  }
}
