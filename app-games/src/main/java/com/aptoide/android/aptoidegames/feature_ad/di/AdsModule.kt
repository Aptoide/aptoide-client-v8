package com.aptoide.android.aptoidegames.feature_ad.di

import com.aptoide.android.aptoidegames.Platform.isHmd
import com.aptoide.android.aptoidegames.feature_ad.Mintegral
import com.aptoide.android.aptoidegames.feature_ad.MintegralImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdsModule {

  @Singleton
  @Provides
  fun provideMintegral(
    mintegralImpl: MintegralImpl,
  ): Mintegral {
    return if (isHmd) {
      object : Mintegral {}
    } else {
      mintegralImpl
    }
  }
}
