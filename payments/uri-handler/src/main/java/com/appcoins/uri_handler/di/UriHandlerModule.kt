package com.appcoins.uri_handler.di

import com.appcoins.uri_handler.handler.UriHandler
import com.appcoins.uri_handler.handler.UriHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UriHandlerModule {

  @Singleton
  @Binds
  fun bindUriHandlerImpl(paymentManager: UriHandlerImpl): UriHandler
}
