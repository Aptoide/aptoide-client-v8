package cm.aptoide.pt.feature_editorial.di

import cm.aptoide.pt.feature_editorial.data.deviceapi.DeviceApiEditorialRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Present only in aptoideGamesDev ⇒ device-API editorial; empty elsewhere ⇒ v7. */
@Module
@InstallIn(SingletonComponent::class)
internal interface OptionalEditorialBackendModule {
  @BindsOptionalOf
  fun optionalDeviceEditorialRepository(): DeviceApiEditorialRepository
}
