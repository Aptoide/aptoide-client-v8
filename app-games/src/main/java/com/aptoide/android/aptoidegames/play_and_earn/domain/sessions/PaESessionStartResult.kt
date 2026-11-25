package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal

/** What starting a Play & Earn session for a game gave (AND-950). */
sealed interface PaESessionStartResult {

  /** A new session is tracking the game. */
  data object Started : PaESessionStartResult

  /** The game already had a session: nothing was asked of the server. */
  data object AlreadyActive : PaESessionStartResult

  /** The backend refused it with a code; retrying will not help. */
  data class Refused(val refusal: PaERefusal) : PaESessionStartResult

  /** Any other failure (network, server error, missing data): may be retried. */
  data object Failed : PaESessionStartResult
}
