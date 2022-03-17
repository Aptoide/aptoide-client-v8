package cm.aptoide.pt.feature_updates.domain.repository

import cm.aptoide.pt.feature_updates.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface UpdatesRepository {

  fun getInstalledApps(): Flow<List<InstalledApp>>
}