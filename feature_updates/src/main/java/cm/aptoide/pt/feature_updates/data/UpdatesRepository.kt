package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_updates.domain.ApkData
import kotlinx.coroutines.flow.Flow

/** The updates available for the installed apps, kept on the device between checks. */
interface UpdatesRepository {

  /** Asks the backend about [apksData], keeps the updates it answers with, and returns them. */
  suspend fun loadUpdates(apksData: List<ApkData>): List<App>

  /** The kept updates. */
  fun getUpdates(): Flow<List<App>>

  /** Forgets the kept updates of [packageNames]. */
  suspend fun remove(packageNames: List<String>)
}
