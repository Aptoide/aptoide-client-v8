package cm.aptoide.pt.feature_apps.di

import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiReviewsRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiSplitsRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Optional device-API repositories. The concrete impls are provided ONLY in the
 * `aptoideGamesDev` source set, so `Optional<…>` is present there (→ device API)
 * and empty everywhere else (→ v7 default). This is the dev-only backend switch —
 * see [RepositoryModule].
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface OptionalBackendModule {
  @BindsOptionalOf
  fun optionalDeviceAppRepository(): DeviceApiAppRepository

  @BindsOptionalOf
  fun optionalDeviceAppsListRepository(): DeviceApiAppsListRepository

  @BindsOptionalOf
  fun optionalDeviceSplitsRepository(): DeviceApiSplitsRepository

  @BindsOptionalOf
  fun optionalDeviceReviewsRepository(): DeviceApiReviewsRepository
}
