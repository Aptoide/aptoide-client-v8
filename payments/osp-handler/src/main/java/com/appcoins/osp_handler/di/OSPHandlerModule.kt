package com.appcoins.osp_handler.di

import com.appcoins.osp_handler.handler.OSPHandler
import com.appcoins.osp_handler.handler.OSPHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface OSPHandlerModule {

  @Singleton
  @Binds
  fun bindOSPHandlerImpl(paymentManager: OSPHandlerImpl): OSPHandler
}
