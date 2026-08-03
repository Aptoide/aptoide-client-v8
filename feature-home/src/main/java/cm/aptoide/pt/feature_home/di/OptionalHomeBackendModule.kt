package cm.aptoide.pt.feature_home.di

import cm.aptoide.pt.feature_home.data.deviceapi.DeviceApiWidgetsRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Present only in aptoideGamesDev ⇒ device-API home; empty elsewhere ⇒ v7. */
@Module
@InstallIn(SingletonComponent::class)
internal interface OptionalHomeBackendModule {
  @BindsOptionalOf
  fun optionalDeviceWidgetsRepository(): DeviceApiWidgetsRepository
}
