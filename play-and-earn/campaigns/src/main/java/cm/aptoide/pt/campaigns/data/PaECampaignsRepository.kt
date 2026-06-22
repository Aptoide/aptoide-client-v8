package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.PaEBundles

interface PaECampaignsRepository {

  suspend fun getCampaigns(): Result<PaEBundles>

  suspend fun getAvailablePackages(): Result<Set<String>>

  suspend fun getCachedApp(packageName: String): Result<PaEApp?>
}
