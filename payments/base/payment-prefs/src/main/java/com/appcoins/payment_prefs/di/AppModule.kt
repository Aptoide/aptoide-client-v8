package com.appcoins.payment_prefs.di

import android.content.Context
import android.content.SharedPreferences
import com.appcoins.payment_prefs.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton
  @Provides
  fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    return context.getSharedPreferences("${BuildConfig.LIBRARY_PACKAGE_NAME}.payments", Context.MODE_PRIVATE)
  }
}
