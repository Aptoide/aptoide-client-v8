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

      cacheCampaignPackages(paeBundles)

      Result.success(paeBundles)
    } catch (e: Throwable) {
      Result.failure(e)
    }
  }

  private suspend fun cacheCampaignPackages(bundles: PaEBundles) {
    val packageNames = buildSet {
      bundles.trending?.apps?.forEach { add(it.packageName) }
      bundles.keepPlaying?.apps?.forEach { add(it.packageName) }
    }

    val entities = packageNames.map { PaEAppEntity(it) }
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
  progress = progress?.toDomainModel()
)

private fun PaEProgressJson.toDomainModel() = PaEProgress(
  current = current,
  target = target,
  type = type,
  status = status
)
