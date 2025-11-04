package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.toDomain
import cm.aptoide.pt.campaigns.data.database.model.toEntity
import cm.aptoide.pt.campaigns.data.model.PaEAppJson
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionProgressJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionStatusJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEProgressJson
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.PaEProgress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPaECampaignsRepository @Inject constructor(
  private val paeCampaignsApi: PaECampaignsApi,
  private val paeMissionDao: PaeMissionDao,
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
        val missions = fetchMissions(packageName)
        Result.success(missions)
      } catch (e: Throwable) {
        e.printStackTrace()
        Result.failure(e)
      }
    }

  override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> = flow {
    getCachedMissions(packageName)?.let { emit(Result.success(it)) }

    try {
      val missions = fetchMissions(packageName)
      emit(Result.success(missions))
    } catch (e: Throwable) {
      e.printStackTrace()
      if (getCachedMissions(packageName) == null) {
        emit(Result.failure(e))
      }
    }
  }.flowOn(dispatcher)

  private suspend fun getCachedMissions(packageName: String): PaEMissions? {
    val cachedMissions = paeMissionDao.getAppMissions(packageName)
    if (cachedMissions.isEmpty()) {
      return null
    }

    val missions = cachedMissions.map { it.toDomain() }
    val checkpoints = missions.filter { it.type == PaEMissionType.CHECKPOINT }
    val regularMissions = missions.filter { it.type != PaEMissionType.CHECKPOINT }

    return PaEMissions(checkpoints = checkpoints, missions = regularMissions)
  }

  private suspend fun fetchMissions(packageName: String): PaEMissions {
    val missions = paeCampaignsApi.getCampaignMissions(packageName).toDomainModel()

    // Replace cache with network results
    paeMissionDao.clearAppMissions(packageName)
    val allMissions = missions.checkpoints + missions.missions
    val entities = allMissions.map { it.toEntity(packageName) }
    paeMissionDao.insertAll(entities)

    return missions
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
