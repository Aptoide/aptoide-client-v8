package cm.aptoide.pt.feature_updates.domain

import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.di.PrioritizedPackagesFilter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Updates @Inject constructor(
  private val packageManager: PackageManager,
  private val updatesRepository: UpdatesRepository,
  private val appsListMapper: AppsListMapper,
  @PrioritizedPackagesFilter private val prioritizedPackages: List<String>,
) {

  val mutex: Mutex = Mutex()

  suspend fun check() = mutex.withLock {
    val apksData = packageManager.getInstalledPackages()
      .filter { it.ifNormalAppOrGame() }
      .map {
        ApkData(
          signature = packageManager.getSignature(it.packageName).uppercase(),
          packageName = it.packageName,
          versionCode = PackageInfoCompat.getLongVersionCode(it)
        )
      }
    val updates = updatesRepository.loadUpdates(apksData)
    updatesRepository.replaceWith(*updates.toTypedArray())
  }

  suspend fun getAppsUpdates(): List<App> = mutex.withLock {
    updatesRepository.getUpdates()
      .let(appsListMapper::map)
      .sortedBy {
        if (it.packageName in prioritizedPackages) {
          LocalDate.now().plusDays(1).toString()
        } else {
          it.modifiedDate
        }
      }
      .reversed()
  }
}
