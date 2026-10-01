package cm.aptoide.pt.feature_updates.di

import cm.aptoide.pt.aptoide_network.di.BackendOverride
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.SilentUpdatePolicy
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * What a build may replace or add. One that binds nothing here keeps the v7 implementation
 * and installs any update silently - see [RepositoryModule] and [SilentUpdatePolicy].
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface BackendOverrideModule {

  @BindsOptionalOf
  @BackendOverride
  fun updatesRepository(): UpdatesRepository

  @BindsOptionalOf
  fun silentUpdatePolicy(): SilentUpdatePolicy
}
