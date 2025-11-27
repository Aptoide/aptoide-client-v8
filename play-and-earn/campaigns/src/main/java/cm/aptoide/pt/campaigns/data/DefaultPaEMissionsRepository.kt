package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.toDomain
import cm.aptoide.pt.campaigns.data.database.model.toEntity
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionProgressJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionStatusJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPaEMissionsRepository @Inject constructor(
  private val paeCampaignsApi: PaECampaignsApi,
  private val paeMissionDao: PaeMissionDao,
  private val dispatcher: CoroutineDispatcher
) : PaEMissionsRepository {

  override suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean
  ): Result<PaEMissions> =
    withContext(dispatcher) {
      try {
        if (!forceRefresh) {
          getCachedMissions(packageName)?.let { return@withContext Result.success(it) }
        }

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

  override suspend fun getCachedMissions(packageName: String): PaEMissions? {
    val cachedMissions = paeMissionDao.getAppMissions(packageName)
    if (cachedMissions.isEmpty()) {
      return null
    }

    val missions = cachedMissions.map { it.toDomain() }
    val checkpoints = missions.filter { it.type == PaEMissionType.CHECKPOINT }
    val regularMissions = missions.filter { it.type != PaEMissionType.CHECKPOINT }

    return PaEMissions(checkpoints = checkpoints, missions = regularMissions)
  }

  override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) {
    withContext(dispatcher) {
      try {
        paeMissionDao.updateMissionStatus(
          packageName = packageName,
          missionTitle = missionTitle,
          status = PaEMissionStatus.COMPLETED
        )
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }

  private suspend fun fetchMissions(packageName: String): PaEMissions {
    val missions = paeCampaignsApi.getCampaignMissions(packageName).toDomainModel()

    val allMissions = missions.checkpoints + missions.missions
    val entities = allMissions.map { it.toEntity(packageName) }
    paeMissionDao.replaceAppMissions(packageName, entities)

    return missions
  }
}

private fun PaEMissionsJson.toDomainModel() = PaEMissions(
  checkpoints = checkpoints.map(PaEMissionJson::toDomainModel),
  missions = missions.map(PaEMissionJson::toDomainModel)
)

private fun PaEMissionJson.toDomainModel() = PaEMission(
  title = title,
  description = description,
  icon = icon.takeIf { !it.isNullOrBlank() },
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
  PaEMissionStatusJson.IN_PROGRESS -> PaEMissionStatus.IN_PROGRESS
  PaEMissionStatusJson.COMPLETED -> PaEMissionStatus.COMPLETED
  null -> null
}
