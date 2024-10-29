package cm.aptoide.pt.installer.network

import cm.aptoide.pt.installer.DynamicSplit

interface SplitsRepository {
  suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplit>
}
