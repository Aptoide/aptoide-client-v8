package com.aptoide.android.aptoidegames.context.di

import com.aptoide.android.aptoidegames.context.PayloadMapperImpl
import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PayloadDataModule {

  @Singleton
  @Provides
  fun providePayloadMapper(): PayloadMapper =
    PayloadMapperImpl()
}
