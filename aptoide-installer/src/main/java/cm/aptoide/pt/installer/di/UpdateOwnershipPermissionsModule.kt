package cm.aptoide.pt.installer.di

import cm.aptoide.pt.installer.platform.UpdateOwnershipPermissions
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UpdateOwnershipPermissionsModule {

  @BindsOptionalOf
  fun updateOwnershipPermissions(): UpdateOwnershipPermissions
}
