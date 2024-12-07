package cm.aptoide.pt.analytics

class AppSizeAnalyticsStringMapper {

  fun mapAppSizeToMBBucketValue(appSize: Long): Long {
    val mb = appSize / (1024 * 1024)
    return ((mb / 100) * 100 + 100)
  }
}
