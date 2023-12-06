package cm.aptoide.pt.network_listener.di

import android.content.Context
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkListenerModule {

  @Provides
  @Singleton
  fun provideNetworkConnectionImpl(
    @ApplicationContext context: Context,
  ): NetworkConnection {
    return NetworkConnectionImpl(context)
  }
}
