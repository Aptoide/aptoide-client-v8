package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaEAppEntity
import cm.aptoide.pt.campaigns.data.database.PaEAppsDao
import cm.aptoide.pt.campaigns.data.model.PaEAppJson
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEProgressJson
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEProgress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPaECampaignsRepository @Inject constructor(
  private val paeCampaignsApi: PaECampaignsApi,
  private val paEAppsDao: PaEAppsDao,
  private val dispatcher: CoroutineDispatcher
) : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> = withContext(dispatcher) {
    try {
      val paeBundles = paeCampaignsApi.getCampaigns().toDomainModel()

      cacheCampaignApps(paeBundles)

      Result.success(paeBundles)
    } catch (e: Throwable) {
      Result.failure(e)
    }
  }

  private suspend fun cacheCampaignApps(bundles: PaEBundles) {
    val entities = (bundles.keepPlaying?.apps.orEmpty() + bundles.trending?.apps.orEmpty())
      // An app can appear in both bundles; keep the one offering the most prizes.
      .groupBy { it.packageName }
      .map { (_, apps) -> apps.maxBy { it.totalPrizes }.toEntity() }

    paEAppsDao.replaceAll(entities)
  }

  override suspend fun getAvailablePackages(): Result<Set<String>> = withContext(dispatcher) {
    try {
      val packages = paEAppsDao.getAvailablePaEPackageNames().toSet()
      Result.success(packages)
    } catch (e: Throwable) {
      Result.failure(e)
    }
  }

  override suspend fun getCachedApp(packageName: String): Result<PaEApp?> =
    withContext(dispatcher) {
      try {
        Result.success(paEAppsDao.getApp(packageName)?.toDomainModel())
      } catch (e: Throwable) {
        Result.failure(e)
      }
    }
}

private fun PaECampaignJson.toDomainModel(): PaEBundles = PaEBundles(
  keepPlaying = keepPlayingCampaign?.let {
    PaEBundle(
      title = "Keep Playing",
      apps = it.map(PaEAppJson::toDomainModel)
    )
  },
  trending = trending?.let {
    PaEBundle(
      title = "Everyone's favourites",
      apps = trending.map(PaEAppJson::toDomainModel)
    )
  }
)

private fun PaEAppJson.toDomainModel() = PaEApp(
  packageName = appInfo.packageName,
  icon = appInfo.icon,
  graphic = appInfo.graphic,
  name = appInfo.name,
  uname = appInfo.uname,
  progress = progress?.toDomainModel(),
  totalPrizes = totalPrizes,
)

private fun PaEProgressJson.toDomainModel() = PaEProgress(
  current = current,
  target = target,
  type = type,
  status = status
)

// `progress` is deliberately dropped: it's volatile/per-user and must not be served from cache.
private fun PaEApp.toEntity() = PaEAppEntity(
  packageName = packageName,
  icon = icon,
  graphic = graphic,
  name = name,
  uname = uname,
  totalPrizes = totalPrizes,
)

// Reconstructed without `progress` (not cached); callers must fetch live progress separately.
private fun PaEAppEntity.toDomainModel() = PaEApp(
  packageName = packageName,
  icon = icon,
  graphic = graphic,
  name = name,
  uname = uname,
  progress = null,
  totalPrizes = totalPrizes,
)
