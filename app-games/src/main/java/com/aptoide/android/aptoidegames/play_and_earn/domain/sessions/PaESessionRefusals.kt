package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The last refusal of each game's session (AND-950), kept so its Rewards tab can say why instead
 * of showing the missions as if they counted. In memory: the app's process forgets it, and the
 * next session start for the game, successful or refused, replaces it.
 */
@Singleton
class PaESessionRefusals @Inject constructor() {

  private val refusals = MutableStateFlow<Map<String, PaERefusal>>(emptyMap())

  fun observe(packageName: String): Flow<PaERefusal?> =
    refusals.map { it[packageName] }.distinctUntilChanged()

  fun record(packageName: String, refusal: PaERefusal) =
    refusals.update { it + (packageName to refusal) }

  fun clear(packageName: String) = refusals.update { it - packageName }
}
