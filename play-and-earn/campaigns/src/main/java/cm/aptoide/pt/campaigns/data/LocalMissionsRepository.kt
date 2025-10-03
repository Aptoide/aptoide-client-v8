package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity
import cm.aptoide.pt.campaigns.data.database.model.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMissionsRepository @Inject constructor(
  private val paeMissionDao: PaeMissionDao
) {

  suspend fun getLocalAppMissions(packageName: String) = withContext(Dispatchers.IO) {
    paeMissionDao.getAppMissions(packageName).map(PaEMissionEntity::toDomain)
  }
}
