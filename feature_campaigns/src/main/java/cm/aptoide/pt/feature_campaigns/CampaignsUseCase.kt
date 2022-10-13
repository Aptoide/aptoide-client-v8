package cm.aptoide.pt.feature_campaigns

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampaignsUseCase @Inject constructor(
  private val repository: CampaignsRepository
) {

  private val cachedListOfCampaigns: MutableMap<String, List<Campaign>> = mutableMapOf()

  suspend fun getCampaign(appPackage: String): Campaign? =
    (cachedListOfCampaigns[appPackage]
      ?: try {
        repository.getCampaigns(appPackage)
          .also { cachedListOfCampaigns[appPackage] = it }
      } catch (e: Throwable) {
        null
      })
      ?.takeIf { it.isNotEmpty() }
      ?.get(0)
}

interface CampaignsRepository {
  suspend fun getCampaigns(appPackage: String): List<Campaign>
}
