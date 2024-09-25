package cm.aptoide.pt.feature_apkfy.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.feature_apkfy.repository.ApkfyPreferencesRepository
import cm.aptoide.pt.feature_apkfy.repository.ApkfyRepository
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apkfy")

interface ApkfyManager {
  suspend fun getApkfy(): ApkfyModel?
}

class ApkfyManagerImpl @Inject constructor(
  private val apkfyRepository: ApkfyRepository,
  private val apkfyPreferencesRepository: ApkfyPreferencesRepository,
) : ApkfyManager {

  override suspend fun getApkfy(): ApkfyModel? = if (apkfyPreferencesRepository.shouldRunApkfy()) {
    apkfyPreferencesRepository.setApkfyRan()
    apkfyRepository.getApkfy()
  } else {
    null
  }
}
