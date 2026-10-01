package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.ApkData
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// On the Play build only the apps that install through Aptoide may be offered as updates;
// Play updates the rest, and this build has no download of its own for them. v7 answers for
// this build's store, with the files and flags Aptoide's installer needs.
@ExperimentalCoroutinesApi
internal class PlayUpdatesRepositoryTest {

  private val billed = randomApp.copy(
    packageName = "com.my.defense",
    origin = AppOrigin.V7,
    bdsFlags = listOf("STORE_BDS"),
    isAppCoins = true,
  )
  private val playInstalled = randomApp.copy(
    packageName = "com.block.juggle",
    origin = AppOrigin.V7,
    bdsFlags = null,
    isAppCoins = false,
  )
  private val overlay = randomApp.copy(
    packageName = ROBLOX_PACKAGE,
    origin = AppOrigin.V7,
    bdsFlags = listOf("STORE_BDS"),
    isAppCoins = true,
  )

  @Test
  fun `Loading the updates keeps those of apps that install through Aptoide`() = coScenario {
    m Given "v7 answering with a billed app, one Play installs and an overlay title"
    val v7 = FakeUpdatesRepository(answers = listOf(billed, playInstalled, overlay))
    val repository = PlayUpdatesRepository(v7)

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(listOf(ApkData("sig", "com.my.defense", 1)))

    m Then "only the billed app's update is returned, and v7 was asked as it is"
    assertEquals(listOf("com.my.defense"), loaded.map { it.packageName })
    assertEquals(listOf("com.my.defense"), v7.asked.single().map { it.packageName })
  }

  @Test
  fun `The kept updates are read the same way`() = coScenario {
    m Given "v7 keeping updates of the three"
    val v7 = FakeUpdatesRepository(kept = listOf(billed, playInstalled, overlay))
    val repository = PlayUpdatesRepository(v7)

    m When "the updates are read"
    val updates = repository.getUpdates().first()

    m Then "only the billed app's update is there"
    assertEquals(listOf("com.my.defense"), updates.map { it.packageName })
  }

  @Test
  fun `Removing an update is left to v7`() = coScenario {
    m Given "v7 keeping an update"
    val v7 = FakeUpdatesRepository(kept = listOf(billed))
    val repository = PlayUpdatesRepository(v7)

    m When "it is removed"
    repository.remove(listOf("com.my.defense"))

    m Then "v7 forgot it"
    assertEquals(listOf(listOf("com.my.defense")), v7.removed)
  }
}

private class FakeUpdatesRepository(
  private val answers: List<App> = emptyList(),
  kept: List<App> = emptyList(),
) : UpdatesRepository {

  val asked = mutableListOf<List<ApkData>>()
  val removed = mutableListOf<List<String>>()
  private val kept = MutableStateFlow(kept)

  override suspend fun loadUpdates(apksData: List<ApkData>): List<App> {
    asked += apksData
    return answers
  }

  override fun getUpdates(): Flow<List<App>> = kept

  override suspend fun remove(packageNames: List<String>) {
    removed += packageNames
    kept.value = kept.value.filterNot { it.packageName in packageNames }
  }
}
