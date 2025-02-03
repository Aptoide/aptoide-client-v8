package cm.aptoide.pt.appcomingsoon.di

import cm.aptoide.pt.appcomingsoon.repository.AppComingSoonPromotionalRepository
import cm.aptoide.pt.appcomingsoon.repository.AptoideAppComingSoonPromotionalRepository
import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  fun provideAppComingSoonPromotionalRepository(
    @RetrofitV7ActionItem retrofit: Retrofit,
  ): AppComingSoonPromotionalRepository {
    return AptoideAppComingSoonPromotionalRepository(retrofit.create(cm.aptoide.pt.appcomingsoon.repository.Retrofit::class.java));
  }
}
