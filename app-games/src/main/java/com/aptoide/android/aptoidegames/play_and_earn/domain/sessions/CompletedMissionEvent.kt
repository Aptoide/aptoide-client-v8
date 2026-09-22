package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission

/**
 * A mission the server confirmed as completed, paired with the game it was completed in.
 * [PaEMission] itself carries no package name, but every consumer needs one to link back to the
 * game.
 */
data class CompletedMissionEvent(
  val mission: PaEMission,
  val packageName: String,
)
