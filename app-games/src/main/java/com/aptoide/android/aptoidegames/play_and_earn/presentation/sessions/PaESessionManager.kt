package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import android.util.Log
import cm.aptoide.pt.campaigns.data.LocalMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaESessionManager @Inject constructor(
  private val paESessionsRepository: PaESessionsRepository,
  private val localMissionsRepository: LocalMissionsRepository
) {

  val sessions = mutableListOf<PaESession>()

  val _completedMissions = MutableSharedFlow<PaEMission>()
  val completedMissions = _completedMissions.asSharedFlow()

  suspend fun createSession(packageName: String) {
    if (sessions.none { it.packageName == packageName }) {
      val session = paESessionsRepository.startSession(packageName).getOrNull()

      session?.let {
        sessions.add(
          PaESession(
            sessionId = session.sessionId,
            packageName = packageName,
            missions = null
          )
        )
      }
    }
  }

  suspend fun syncSessions(currentPackage: String?) {
    sessions.find { it.packageName == currentPackage }?.let { session ->
      session.usageTimeSinceLastSync += 6

      if (session.shouldSync()) {
        Log.d("lol", "syncSessions: going to sync")
        val syncResult = paESessionsRepository.heartbeatSession(
          session.sessionId,
          session.packageName,
          session.syncSequence,
          session.usageTimeSinceLastSync
        )

        if (syncResult.isSuccess) {
          session.lastSyncTime = System.currentTimeMillis()
          session.syncSequence++
          session.usageTimeSinceLastSync = 0

          //Update missions time in missions DB

          syncResult.getOrNull()?.let { syncInfo ->
            syncInfo.events.forEach { event ->
              if (!session.completedMissions.contains(event.missionTitle)) {
                session.completedMissions.add(event.missionTitle)

                val mission = localMissionsRepository.getLocalAppMissions(session.packageName)
                  .find { it.title == event.missionTitle }

                //Update mission in missions local DB
                //Emit completed mission. Backend should return mission info
                mission?.let {
                  _completedMissions.emit(it)
                }
              }
            }
          }
        }
      }else {
        Log.d("lol", "syncSessions: should not sync")
      }
    }
  }
}
