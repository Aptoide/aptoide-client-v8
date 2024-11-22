package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON

interface SplitsRepository {
  suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplitJSON>
}
