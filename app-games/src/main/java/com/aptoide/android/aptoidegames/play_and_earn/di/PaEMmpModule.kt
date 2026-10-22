package com.aptoide.android.aptoidegames.play_and_earn.di

import cm.aptoide.pt.aptoide_network.di.SimpleOkHttp
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignApiRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.mmp.MissionsPaEMmpClickUrlSource
import com.aptoide.android.aptoidegames.play_and_earn.domain.mmp.PaEMmpClickUrlSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PaEMmpModule {

  // Each campaign's own link, as Aptoide Connect set it, from /missions.
  @Binds
  fun bindPaEMmpClickUrlSource(source: MissionsPaEMmpClickUrlSource): PaEMmpClickUrlSource

  companion object {

    /**
     * The campaigns client without its HTTP logging: the click URL carries the wallet and the
     * advertising id, and that logger prints full URLs in release builds too.
     */
    @Provides
    @Singleton
    @PaEMmpClickRepository
    fun providePaEMmpClickRepository(
      @SimpleOkHttp okHttpClient: OkHttpClient,
    ): CampaignRepository = CampaignApiRepository(
      okHttpClient.newBuilder()
        .apply { interceptors().removeAll { it is HttpLoggingInterceptor } }
        .followSslRedirects(false)
        .build()
    )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaEMmpClickRepository
