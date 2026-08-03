package cm.aptoide.pt.feature_categories.di

import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Present only in aptoideGamesDev ⇒ device-API categories; empty elsewhere ⇒ v7. */
@Module
@InstallIn(SingletonComponent::class)
internal interface OptionalCategoriesBackendModule {
  @BindsOptionalOf
  fun optionalDeviceCategoriesRepository(): DeviceApiCategoriesRepository
}
