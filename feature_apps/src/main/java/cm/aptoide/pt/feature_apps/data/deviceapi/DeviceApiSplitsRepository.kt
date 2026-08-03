package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.SplitsRepository
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON

/**
 * Device-API splits (aptoideGamesDev only). The device app-detail response already
 * embeds the device-resolved required splits (in `aab.baseSplits`), and there is no
 * device `getDynamicSplits`-by-md5 endpoint. So this returns empty rather than
 * calling v7 `app/getDynamicSplits` with a device md5 v7 can't resolve — which
 * otherwise threw and broke the app-view for every AAB app (leftover: getDynamicSplits).
 */
class DeviceApiSplitsRepository : SplitsRepository {
  override suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplitJSON> = emptyList()
}
