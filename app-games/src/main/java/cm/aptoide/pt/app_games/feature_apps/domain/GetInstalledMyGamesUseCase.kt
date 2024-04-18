package cm.aptoide.pt.app_games.feature_apps.domain

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.feature_apps.data.MyGamesApp
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInstalledMyGamesUseCase @Inject constructor(
  private val packageManager: PackageManager,
  private val installManager: InstallManager,
  private val repository: CategoriesRepository,
) {

  fun getMyGamesAppsList(): Flow<List<MyGamesApp>> {
    val apps = installManager.installedApps.toMutableSet()
    return installManager.appsChanges
      .map { apps.apply { add(it) } }
      .onStart { emit(apps) }
      .map { set ->
        filterGames(set.mapNotNull(App::packageInfo))
          .map {
            MyGamesApp(
              name = it.applicationInfo.loadLabel(packageManager).toString(),
              packageName = it.packageName,
              versionName = it.versionName
            )
          }
      }
  }

  private suspend fun filterGames(appsList: List<PackageInfo>): List<PackageInfo> {
    val gamesPackageInfoList = ArrayList<PackageInfo>()
    val undefinedPackageInfoMap = HashMap<String, PackageInfo>()
    appsList.forEach { packageInfo ->
      when (packageInfo.applicationInfo.category) {
        ApplicationInfo.CATEGORY_GAME -> gamesPackageInfoList.add(packageInfo)
        else -> undefinedPackageInfoMap[packageInfo.packageName] = packageInfo
      }
    }
    try {
      repository.getAppsCategories(undefinedPackageInfoMap.keys.toList())
        .map { appCategory ->
          when (appCategory.type) {
            "GAME" -> undefinedPackageInfoMap[appCategory.name]?.let {
              gamesPackageInfoList.add(
                it
              )
            }

            else -> {}
          }
        }
    } catch (e: IOException) {
      e.printStackTrace()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
    return gamesPackageInfoList
  }
}
