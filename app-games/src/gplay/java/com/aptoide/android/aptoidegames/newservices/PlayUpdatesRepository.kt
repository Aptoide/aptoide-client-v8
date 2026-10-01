package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.ApkData
import com.aptoide.android.aptoidegames.installer.excludedFromPlayCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The updates of the Play build: those v7 finds in this build's store, kept to the apps that
 * install through Aptoide. Play updates the rest, and this build has no download of its own
 * for them. The new services know which apps are outdated but carry no files, and their Play
 * catalog does not flag billing, so an update read there could not be installed.
 */
internal class PlayUpdatesRepository(
  private val v7: UpdatesRepository,
) : UpdatesRepository {

  override suspend fun loadUpdates(apksData: List<ApkData>): List<App> =
    v7.loadUpdates(apksData).installable()

  override fun getUpdates(): Flow<List<App>> = v7.getUpdates().map { it.installable() }

  override suspend fun remove(packageNames: List<String>) = v7.remove(packageNames)

  private fun List<App>.installable() = filter { it.excludedFromPlayCatalog() }
}
