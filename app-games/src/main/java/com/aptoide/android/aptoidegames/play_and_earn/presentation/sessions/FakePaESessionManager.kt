package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePaESessionManager @Inject constructor(
  private val paECampaignsRepository: PaECampaignsRepository
) {

  val sessions = mutableListOf<PaESession>()

  val _completedMissions = MutableSharedFlow<PaEMission>()
  val completedMissions = _completedMissions.asSharedFlow()

  suspend fun createSession(packageName: String) {
    val missions = paECampaignsRepository.getCampaignMissions(packageName).getOrNull()
    if (sessions.none { it.packageName == packageName }) {
      sessions.add(
        PaESession(
          sessionId = UUID.randomUUID().toString(),
          packageName = packageName,
          missions = missions ?: PaEMissions(emptyList(), emptyList())
        )
      )
    }
  }

  suspend fun syncSessions(currentPackage: String?) {
    sessions.forEach { session ->
      if (session.packageName == currentPackage) {
        session.totalSessionTime += 6

        session.missions?.missions?.forEach { mission ->
          if (mission.type == PaEMissionType.PLAY_TIME) {
            if (!session.completedMissions.contains(mission.title)) {
              mission.progress?.target?.let {
                if (session.totalSessionTime >= it) {
                  session.completedMissions.add(mission.title)
                  _completedMissions.emit(mission)
                }
              }
            }
          }
        }
      }
    }
  }
}
