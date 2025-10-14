package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
internal class FakePaECampaignsRepository : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> =
    Result.success(paeCampaigns)

  override suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean
  ): Result<PaEMissions> =
    Result.success(paeMissions)

  override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
    flowOf(Result.success(paeMissions))

  override suspend fun getAvailablePackages(): Result<Set<String>> {
    val packages = mutableSetOf<String>()

    packages.addAll(paeCampaigns.trending?.apps?.map { it.packageName }.orEmpty())
    packages.addAll(paeCampaigns.keepPlaying?.apps?.map { it.packageName }.orEmpty())

    return Result.success(packages)
  }
}
