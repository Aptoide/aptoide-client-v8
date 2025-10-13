package com.aptoide.android.aptoidegames.device_info.di

import cm.aptoide.pt.environment_info.DeviceIdProvider
import com.aptoide.android.aptoidegames.device_info.AGDeviceIdProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DeviceInfoModule {

  @Singleton
  @Provides
  fun provideDeviceIdProvider(
    firebaseMessaging: FirebaseMessaging,
  ): DeviceIdProvider {
    return AGDeviceIdProvider(
      firebaseMessaging = firebaseMessaging
    )
  }
}
