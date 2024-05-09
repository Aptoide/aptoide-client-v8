package com.aptoide.android.aptoidegames.di

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

  @Singleton
  @Provides
  fun provideFirebaseInstallations(): FirebaseInstallations =
    FirebaseInstallations.getInstance()

  @Singleton
  @Provides
  fun provideFirebaseMessaging(): FirebaseMessaging =
    FirebaseMessaging.getInstance()

}
