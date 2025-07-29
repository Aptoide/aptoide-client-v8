package com.aptoide.android.aptoidegames.play_and_earn.domain

import com.aptoide.android.aptoidegames.R
import kotlin.random.Random

data class Missions(
  val checkpoints: List<Mission>,
  val missions: List<Mission>,
)

data class Mission(
  val id: Int = Random.nextInt(),
  val title: String,
  val description: String? = null,
  val icon: Any? = null,
  val type: MissionType,
  val arguments: Map<String, Any>? = null,
  val units: Int? = null,
  val sku: String? = null,
  val ts: String? = null,
  val status: MissionStatus
)

enum class MissionType {
  PLAY_TIME,
  STREAK,
  CHECKPOINT,
  NIGHT_OWL
}

enum class MissionStatus {
  PENDING,
  COMPLETED,
}

val missions = Missions(
  checkpoints = listOf(
    Mission(
      title = "Checkpoint 1",
      type = MissionType.CHECKPOINT,
      icon = "",
      units = 10,
      status = MissionStatus.PENDING
    )
  ),
  missions = listOf(
    Mission(
      title = "Recruit",
      description = "Play during 20 minutes",
      type = MissionType.PLAY_TIME,
      icon = R.drawable.book,
      units = 5,
      status = MissionStatus.PENDING
    ),
    Mission(
      title = "Night Owl",
      description = "Play during 1 hour",
      type = MissionType.NIGHT_OWL,
      icon = R.drawable.cauldron,
      units = 12,
      status = MissionStatus.COMPLETED
    )
  )
)
