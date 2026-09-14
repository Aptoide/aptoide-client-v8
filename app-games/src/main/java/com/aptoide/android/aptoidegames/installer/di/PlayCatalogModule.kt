package com.aptoide.android.aptoidegames.installer.di

import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface PlayCatalogModule {

  // Bound only in the gplay source set; other distributions resolve Optional.empty
  @BindsOptionalOf
  fun playCatalogChecker(): PlayCatalogChecker
}
