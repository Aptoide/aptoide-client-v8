package cm.aptoide.pt.feature_apkfy.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.feature_apkfy.repository.ApkfyRepository
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apkfy")

interface ApkfyManager {
  suspend fun getApkfy(): ApkfyModel?
}

class ApkfyManagerImpl @Inject constructor(
  private val apkfyRepository: ApkfyRepository,
) : ApkfyManager {

  override suspend fun getApkfy(): ApkfyModel = apkfyRepository.getApkfy()
}
