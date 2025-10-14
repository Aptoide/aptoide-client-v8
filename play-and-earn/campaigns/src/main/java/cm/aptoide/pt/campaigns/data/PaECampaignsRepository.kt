package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow

interface PaECampaignsRepository {

  suspend fun getCampaigns(): Result<PaEBundles>

  suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean = false
  ): Result<PaEMissions>

  fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>>

  suspend fun getAvailablePackages(): Result<Set<String>>
}
