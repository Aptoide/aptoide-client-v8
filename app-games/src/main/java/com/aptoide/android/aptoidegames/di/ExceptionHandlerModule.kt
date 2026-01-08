package com.aptoide.android.aptoidegames.di

import cm.aptoide.pt.exception_handler.ExceptionHandler
import com.aptoide.android.aptoidegames.firebase.FirebaseExceptionHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExceptionHandlerModule {

  @Binds
  @Singleton
  abstract fun bindExceptionHandler(
    firebaseExceptionHandler: FirebaseExceptionHandler
  ): ExceptionHandler
}
