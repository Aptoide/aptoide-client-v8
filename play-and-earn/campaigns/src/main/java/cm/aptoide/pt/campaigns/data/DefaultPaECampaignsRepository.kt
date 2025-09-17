package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaEAppJson
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionProgressJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionStatusJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEProgressJson
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.PaEProgress
import cm.aptoide.pt.campaigns.domain.PaEApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPaECampaignsRepository @Inject constructor(
  private val paeCampaignsApi: PaECampaignsApi,
  private val dispatcher: CoroutineDispatcher
) : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> = withContext(dispatcher) {
    try {
      Result.success(paeCampaignsApi.getCampaigns().toDomainModel())
    } catch (e: Throwable) {
      Result.failure(e)
    }
  }

  override suspend fun getCampaignMissions(packageName: String): Result<PaEMissions> =
    withContext(dispatcher) {
      try {
        Result.success(paeCampaignsApi.getCampaignMissions(packageName).toDomainModel())
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

private fun PaEMissionsJson.toDomainModel() = PaEMissions(
  checkpoints = checkpoints.map(PaEMissionJson::toDomainModel),
  missions = missions.map(PaEMissionJson::toDomainModel)
)

private fun PaEMissionJson.toDomainModel() = PaEMission(
  title = title,
  description = description,
  icon = icon,
  type = this.getType(),
  arguments = arguments,
  units = units,
  progress = progress?.toDomainModel()
)

private fun PaEMissionProgressJson.toDomainModel() = PaEMissionProgress(
  current = current,
  target = target,
  type = type,
  status = this.getType()
)

private fun PaEMissionJson.getType() = when (type) {
  PaEMissionTypeJson.PLAY_TIME -> PaEMissionType.PLAY_TIME
  PaEMissionTypeJson.STREAK -> PaEMissionType.STREAK
  PaEMissionTypeJson.CHECKPOINT -> PaEMissionType.CHECKPOINT
}

private fun PaEMissionProgressJson.getType() = when (status) {
  PaEMissionStatusJson.PENDING -> PaEMissionStatus.PENDING
  PaEMissionStatusJson.ONGOING -> PaEMissionStatus.ONGOING
  PaEMissionStatusJson.COMPLETED -> PaEMissionStatus.COMPLETED
  null -> null
}
