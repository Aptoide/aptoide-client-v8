package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.PaEBundles

@Suppress("unused")
internal class FakePaECampaignsRepository : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> =
    Result.success(paeCampaigns)

  override suspend fun getAvailablePackages(): Result<Set<String>> {
    val packages = mutableSetOf<String>()

    packages.addAll(paeCampaigns.trending?.apps?.map { it.packageName }.orEmpty())
    packages.addAll(paeCampaigns.keepPlaying?.apps?.map { it.packageName }.orEmpty())

    return Result.success(packages)
  }

  override suspend fun getCachedApp(packageName: String): Result<PaEApp?> {
    val app = (paeCampaigns.keepPlaying?.apps.orEmpty() + paeCampaigns.trending?.apps.orEmpty())
      .firstOrNull { it.packageName == packageName }
    return Result.success(app)
  }
}
