package cm.aptoide.pt.settings.domain

import cm.aptoide.pt.settings.repository.UserPreferencesRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ThemePreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setIsDarkTheme(it) }
      ?: userPreferencesRepository.removeIsDarkTheme()

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isDarkTheme()
}

// GENERAL SECTION
@ViewModelScoped
class CompatibleAppsPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setOnlyShowCompatibleApps(it) }
      ?: userPreferencesRepository.setOnlyShowCompatibleApps(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.getShowCompatibleApps()
}

@ViewModelScoped
class DownloadOverWifiPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setDownloadOnlyOverWifi(it) }
      ?: userPreferencesRepository.setDownloadOnlyOverWifi(true)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isDownloadOnlyOverWifi()
}

@ViewModelScoped
class BetaVersionsPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setBetaVersions(it) }
      ?: userPreferencesRepository.setBetaVersions(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isBetaVersions()
}

@ViewModelScoped
class NativeInstallerPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setUseNativeInstaller(it) }
      ?: userPreferencesRepository.setUseNativeInstaller(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isUseNativeInstaller()
}

// UPDATES SECTION
@ViewModelScoped
class SystemAppsPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setSystemApps(it) }
      ?: userPreferencesRepository.setSystemApps(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isSystemApps()
}

// NOTIFICATIONS SECTION
@ViewModelScoped
class CampaignsPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setCampaigns(it) }
      ?: userPreferencesRepository.setCampaigns(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isCampaigns()
}

@ViewModelScoped
class AppUpdatesPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setAppUpdates(it) }
      ?: userPreferencesRepository.setAppUpdates(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isAppUpdates()
}

@ViewModelScoped
class UpdateAptoidePreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setUpdateAptoide(it) }
      ?: userPreferencesRepository.setUpdateAptoide(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isUpdateAptoide()
}

// STORAGE SECTION
@ViewModelScoped
class CacheSizePreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : IntPreferencesUseCase {

  override suspend fun set(value: Int) = userPreferencesRepository.setMaxCacheSize(value)

  override fun get(): Flow<Int> = userPreferencesRepository.getMaxCacheSize()
}

// ADULT CONTENT SECTION
@ViewModelScoped
class ShowAdultContentPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setShowAdultContent(it) }
      ?: userPreferencesRepository.setShowAdultContent(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isShowAdultContent()
}

@ViewModelScoped
class UserPinPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : StringPreferencesUseCase {

  override suspend fun set(str: String) = userPreferencesRepository.setUserPinCode(str)

  override fun get(): Flow<String> = userPreferencesRepository.getUserPinCode()
}

// ROOT SECTION
@ViewModelScoped
class RootInstallationPreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setAllowRootInstallation(it) }
      ?: userPreferencesRepository.setAllowRootInstallation(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isAllowRootInstallation()
}

class AutoUpdatePreferencesUseCase @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
) : FlagPreferencesUseCase {

  override suspend fun set(flag: Boolean?) =
    flag?.let { userPreferencesRepository.setEnableAutoUpdate(it) }
      ?: userPreferencesRepository.setEnableAutoUpdate(false)

  override fun get(): Flow<Boolean?> = userPreferencesRepository.isEnableAutoUpdate()
}
