package com.aptoide.android.aptoidegames.gamesfeed.presentation

import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class GamesFeedVisibilityState(
  val shouldShow: Boolean,
  val installedTrackedPackages: List<String> = emptyList()
)

@Singleton
class GamesFeedManager @Inject constructor(
  private val installManager: InstallManager,
  private val gamesFeedRepository: GamesFeedRepository,
  private val firebaseMessaging: FirebaseMessaging,
) {

  companion object {
    const val TOPIC_PREFIX = "gamesfeed_"
  }

  /**
   * Returns a Flow that emits the games feed visibility based on whether any of the
   * tracked games (fetched from the ForYou games API) are installed.
   *
   * Logic:
   * - Fetch the tracked package list from the API.
   * - Subscribe to Firebase topics for all currently installed tracked games.
   * - If any tracked game is installed: emit true.
   * - If none are installed: keep observing app installs (without emitting false,
   *   since the ViewModel starts with null which the UI treats as "don't show").
   *   When a tracked game gets installed, subscribe to its topic and emit true.
   * - After emitting true, continue observing for new tracked game installs
   *   to subscribe to their topics (without re-emitting).
   */
  fun shouldShowGamesFeed(): Flow<GamesFeedVisibilityState> = flow {
    val trackedGames = try {
      gamesFeedRepository.getTrackedGames()
    } catch (e: Exception) {
      Timber.e(e, "Failed to fetch tracked games from API.")
      emit(GamesFeedVisibilityState(shouldShow = false))
      return@flow
    }

    if (trackedGames.isEmpty()) {
      Timber.d("No tracked games returned from API. Emitting false.")
      emit(GamesFeedVisibilityState(shouldShow = false))
      return@flow
    }

    val trackedPackageNames = trackedGames.map { it.packageName }.toSet()
    val installedPackages = installManager.installedApps.map { it.packageName }.toSet()
    val installedTrackedPackages = installedPackages.filter { it in trackedPackageNames }

    if (installedTrackedPackages.isNotEmpty()) {
      Timber.d("Tracked games already installed: $installedTrackedPackages. Showing games feed.")
      subscribeToTopics(installedTrackedPackages)
      emit(GamesFeedVisibilityState(shouldShow = true, installedTrackedPackages = installedTrackedPackages))

      // Keep observing for new tracked game installs to subscribe to their topics
      observeNewTrackedInstalls(trackedPackageNames, installedPackages)
    } else {
      Timber.d("No tracked game installed yet. Observing for installations...")

      // Wait for the first tracked game install
      val firstInstalled = installManager.appsChanges
        .filter { app -> app.packageName in trackedPackageNames }
        .filter { app -> app.packageInfo != null }
        .first()

      val packageName = firstInstalled.packageName
      Timber.d("A tracked game ($packageName) has been installed! Showing games feed.")
      subscribeToTopics(listOf(packageName))
      emit(GamesFeedVisibilityState(shouldShow = true, installedTrackedPackages = listOf(packageName)))

      // Keep observing for new tracked game installs to subscribe to their topics
      observeNewTrackedInstalls(trackedPackageNames, setOf(packageName))
    }
  }

  /**
   * Continues observing for new tracked game installs to subscribe to their Firebase topics.
   * Does not emit any values — this is purely for topic subscription.
   */
  private suspend fun observeNewTrackedInstalls(
    trackedSet: Set<String>,
    alreadyInstalled: Set<String>,
  ) {
    val alreadySubscribed = alreadyInstalled.filter { it in trackedSet }.toMutableSet()

    installManager.appsChanges
      .filter { app -> app.packageName in trackedSet }
      .filter { app -> app.packageInfo != null }
      .filter { app -> app.packageName !in alreadySubscribed }
      .collect { app ->
        alreadySubscribed.add(app.packageName)
        subscribeToTopics(listOf(app.packageName))
        Timber.d("New tracked game installed: ${app.packageName}. Subscribed to topic.")
      }
  }

  private fun subscribeToTopics(packageNames: Collection<String>) {
    packageNames.forEach { packageName ->
      firebaseMessaging.subscribeToTopic("$TOPIC_PREFIX$packageName")
      Timber.d("Subscribed to Firebase topic: $TOPIC_PREFIX$packageName")
    }
  }
}
