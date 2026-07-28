package com.aptoide.android.aptoidegames.installer.gplay.di

import cm.aptoide.pt.installer.platform.UpdateOwnershipPermissions
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Google Play rejects uploads declaring ENFORCE_UPDATE_OWNERSHIP and
// UPDATE_PACKAGES_WITHOUT_USER_ACTION (restricted permissions), removed from
// this flavor's manifest — the installer must not call the APIs that need them
@Module
@InstallIn(SingletonComponent::class)
internal interface UpdateOwnershipModule {

  @Binds
  @Singleton
  fun bindUpdateOwnershipPermissions(impl: DisabledUpdateOwnershipPermissions): UpdateOwnershipPermissions
}

class DisabledUpdateOwnershipPermissions @javax.inject.Inject constructor() :
  UpdateOwnershipPermissions {
  override val enabled: Boolean = false
}
