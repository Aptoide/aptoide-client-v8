package com.aptoide.android.aptoidegames.promo_codes.di

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.promo_codes.analytics.PromoCodeAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PromoCodesModule {

  @Singleton
  @Provides
  fun providesPromoCodeAnalytics(biAnalytics: BIAnalytics): PromoCodeAnalytics = PromoCodeAnalytics(
    biAnalytics = biAnalytics,
  )
}
