package cm.aptoide.pt.feature_search.di

import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSearchRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Present only in aptoideGamesDev ⇒ device-API search; empty elsewhere ⇒ v7. */
@Module
@InstallIn(SingletonComponent::class)
internal interface OptionalSearchBackendModule {
  @BindsOptionalOf
  fun optionalDeviceSearchRepository(): DeviceApiSearchRepository
}
