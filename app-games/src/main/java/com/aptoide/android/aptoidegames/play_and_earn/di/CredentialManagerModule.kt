package com.aptoide.android.aptoidegames.play_and_earn.di

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CredentialManagerModule {

  @Provides
  @Singleton
  fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
    return CredentialManager.create(context)
  }
}
