package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMissions

interface PaECampaignsRepository {

  suspend fun getCampaigns(): Result<PaEBundles>

  suspend fun getCampaignMissions(packageName: String): Result<PaEMissions>

  suspend fun getCampaignPackages(): Result<List<String>>
}
