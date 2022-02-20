package cm.aptoide.pt.feature_search.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.feature_search.data.AptoideSearchRepository
import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.SearchHistoryDatabase
import cm.aptoide.pt.feature_search.data.fake.FakeLocalSearchHistory
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.service.SearchRetrofitService
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {


  @Singleton
  @Provides
  fun provideSearchRepository(
    localSearchHistoryRepository: LocalSearchHistoryRepository,
    remoteSearchRepository: RemoteSearchRepository
  ): SearchRepository {
    return AptoideSearchRepository(localSearchHistoryRepository, remoteSearchRepository)
  }

  @Singleton
  @Provides
  fun provideRemoteSearchRepository(): RemoteSearchRepository {
    return SearchRetrofitService()
  }

  @Singleton
  @Provides
  fun provideLocalSearchHistoryRepository(database: SearchHistoryDatabase): LocalSearchHistoryRepository {
    return FakeLocalSearchHistory()
    // return database.searchDao()
  }

  @Singleton
  @Provides
  fun provideSearchHistoryDatabase(@ApplicationContext appContext: Context): SearchHistoryDatabase {
    return Room.databaseBuilder(appContext, SearchHistoryDatabase::class.java, "aptoide_search.db")
      .build()
  }

}