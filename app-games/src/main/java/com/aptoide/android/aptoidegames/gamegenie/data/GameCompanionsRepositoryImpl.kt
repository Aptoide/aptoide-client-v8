package com.aptoide.android.aptoidegames.gamegenie.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamegenie.data.database.CachedCompanionGameDao
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.CachedCompanionGameEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val COMPANION_GAMES_CACHE_TTL_MS = 3L * 24 * 60 * 60 * 1000

private fun isCompanionGamesCacheValid(updatedAtMs: Long, nowMs: Long): Boolean =
  updatedAtMs > 0L && (nowMs - updatedAtMs) < COMPANION_GAMES_CACHE_TTL_MS

@Singleton
class GameCompanionsRepositoryImpl @Inject constructor(
  private val gameGenieManager: GameGenieManager,
  private val cachedCompanionGameDao: CachedCompanionGameDao,
  private val packageManager: PackageManager,
  private val installManager: InstallManager,
  private val categoriesRepository: CategoriesRepository,
) : GameCompanionsRepository {

  override fun getCompanionGames(): Flow<List<GameCompanion>> {
    val apps = installManager.installedApps.toMutableSet()

    val installedAppsFlow: Flow<List<PackageInfo>> = installManager.appsChanges
      .map { apps.apply { add(it) } }
      .onStart { emit(apps) }
      .map { set -> filterGames(set.mapNotNull(App::packageInfo)) }

    val liveFlow = combine(
      gameGenieManager.getAllGameCompanions(),
      installedAppsFlow
    ) { companionsFromDb, installedPackages ->
      buildGameCompanionsList(companionsFromDb, installedPackages)
    }.onEach { games ->
      val nowMs = System.currentTimeMillis()
      val cacheEntries = games.map {
        CachedCompanionGameEntity(
          packageName = it.packageName,
          name = it.name,
          versionName = it.versionName,
          cachedAtMs = nowMs,
        )
      }
      cachedCompanionGameDao.replaceAll(cacheEntries)
    }

    return flow {
      val nowMs = System.currentTimeMillis()
      val cachedAtMs = cachedCompanionGameDao.getLatestTimestamp()
      if (isCompanionGamesCacheValid(cachedAtMs, nowMs)) {
        val cachedGames = cachedCompanionGameDao.getAllCachedOnce()
          .mapNotNull(::toInstalledGameCompanion)
        if (cachedGames.isNotEmpty()) {
          emit(cachedGames)
        }
      }
      emitAll(liveFlow)
    }.flowOn(Dispatchers.IO)
  }

  override suspend fun warmUpCache() {
    withContext(Dispatchers.IO) {
      val nowMs = System.currentTimeMillis()
      val cachedAtMs = cachedCompanionGameDao.getLatestTimestamp()
      if (isCompanionGamesCacheValid(cachedAtMs, nowMs)) return@withContext
      getCompanionGames().take(1).collect {}
    }
  }

  private fun buildGameCompanionsList(
    companionsFromDb: List<GameCompanionEntity>,
    installedPackages: List<PackageInfo>,
  ): List<GameCompanion> {
    val installedMap = installedPackages.associateBy { it.packageName }

    val orderedFromDb = companionsFromDb
      .filter { installedMap.containsKey(it.gamePackageName) }
      .map { entity ->
        val pkg = installedMap[entity.gamePackageName]!!
        pkg.toGameCompanion()
      }

    val missingFromDb = installedPackages
      .filterNot { pkg -> companionsFromDb.any { it.gamePackageName == pkg.packageName } }
      .sortedByDescending { it.firstInstallTime }
      .map { pkg -> pkg.toGameCompanion() }

    return orderedFromDb + missingFromDb
  }

  private suspend fun filterGames(appsList: List<PackageInfo>): List<PackageInfo> {
    val gamesPackageInfoList = ArrayList<PackageInfo>()
    val undefinedPackageInfoMap = HashMap<String, PackageInfo>()
    appsList.forEach { packageInfo ->
      when (packageInfo.applicationInfo?.category) {
        ApplicationInfo.CATEGORY_GAME -> gamesPackageInfoList.add(packageInfo)
        else -> undefinedPackageInfoMap[packageInfo.packageName] = packageInfo
      }
    }
    try {
      categoriesRepository.getAppsCategories(undefinedPackageInfoMap.keys.toList())
        .forEach { appCategory ->
          when (appCategory.type) {
            "GAME" -> undefinedPackageInfoMap[appCategory.name]?.let {
              gamesPackageInfoList.add(it)
            }
          }
        }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return gamesPackageInfoList
  }

  private fun toInstalledGameCompanion(entity: CachedCompanionGameEntity): GameCompanion? {
    val packageInfo = runCatching {
      packageManager.getPackageInfo(entity.packageName, 0)
    }.getOrNull() ?: return null

    return packageInfo.toGameCompanion(
      name = entity.name,
      versionName = entity.versionName
    )
  }

  private fun PackageInfo.toGameCompanion(
    name: String = applicationInfo?.loadLabel(packageManager)?.toString() ?: packageName,
    versionName: String? = this.versionName,
  ): GameCompanion = GameCompanion(
    name = name,
    packageName = packageName,
    versionName = versionName,
    image = applicationInfo?.loadIcon(packageManager)
  )
}
