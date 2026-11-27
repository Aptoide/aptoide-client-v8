package com.aptoide.android.aptoidegames.play_and_earn.di

import com.aptoide.android.aptoidegames.play_and_earn.domain.DeviceRiskReporter
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEDeviceRiskReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PaEDeviceModule {

  @Binds
  fun bindDeviceRiskReporter(reporter: PaEDeviceRiskReporter): DeviceRiskReporter
}
