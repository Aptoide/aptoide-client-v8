package cm.aptoide.pt.osp_handler.di

import cm.aptoide.pt.osp_handler.handler.OSPHandler
import cm.aptoide.pt.osp_handler.handler.OSPHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface OSPHandlerModule {

  @Singleton
  @Binds
  fun bindOSPHandlerImpl(paymentManager: OSPHandlerImpl): OSPHandler
}
