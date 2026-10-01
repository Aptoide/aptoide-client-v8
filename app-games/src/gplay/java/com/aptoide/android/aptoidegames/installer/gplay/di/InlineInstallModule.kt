package com.aptoide.android.aptoidegames.installer.gplay.di

import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import com.aptoide.android.aptoidegames.installer.gplay.AptoideCatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.CachingCatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.CatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.PlayInlineConfigApi
import com.aptoide.android.aptoidegames.installer.gplay.PlayInlineInstallResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface InlineInstallModule {

  @Binds
  @Singleton
  fun bindInlineInstallResolver(impl: PlayInlineInstallResolver): InlineInstallResolver

  @Binds
  @Singleton
  fun bindCatalogTokenRepository(impl: CachingCatalogTokenRepository): CatalogTokenRepository

  @Binds
  @Singleton
  fun bindPlayCatalogChecker(impl: CachingCatalogTokenRepository): PlayCatalogChecker

  companion object {

    // Swap the origin for [FakeCatalogTokenRepository] to exercise the inline flow and the
    // "Google Play" labeling locally without the backend (every app becomes Play catalog)
    @Provides
    @Singleton
    fun provideCachingCatalogTokenRepository(
      origin: AptoideCatalogTokenRepository,
    ): CachingCatalogTokenRepository = CachingCatalogTokenRepository(
      origin = origin,
      scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
      now = System::currentTimeMillis,
    )

    @Provides
    @Singleton
    fun providePlayInlineConfigApi(
      @DeviceApiRetrofit retrofit: Retrofit,
    ): PlayInlineConfigApi = retrofit.create(PlayInlineConfigApi::class.java)
  }
}
