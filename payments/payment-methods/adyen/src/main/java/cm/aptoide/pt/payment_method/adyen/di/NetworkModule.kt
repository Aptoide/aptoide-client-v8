package cm.aptoide.pt.payment_method.adyen.di

import cm.aptoide.pt.payment_manager.di.RetrofitAPICatappult
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2RepositoryImpl
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2RepositoryImpl.AdyenV2Api
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

  @Singleton
  @Binds
  fun bindAdyenV2Repository(repository: AdyenV2RepositoryImpl): AdyenV2Repository

  companion object {
    @Singleton
    @Provides
    fun provideBrokerApi(@RetrofitAPICatappult retrofit: Retrofit): AdyenV2Api =
      retrofit.create(AdyenV2Api::class.java)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdyenKey
