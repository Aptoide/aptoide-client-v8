package cm.aptoide.pt.feature_search.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.RetrofitBuzz
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_search.data.AptoideSearchRepository
import cm.aptoide.pt.feature_search.data.database.SearchHistoryDatabase
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.service.SearchRetrofitService
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun provideSearchRepository(
    searchHistoryRepository: SearchHistoryRepository,
    remoteSearchRepository: RemoteSearchRepository,
  ): SearchRepository {
    return AptoideSearchRepository(searchHistoryRepository, remoteSearchRepository)
  }

  @Singleton
  @Provides
  fun provideRemoteSearchRepository(
    @RetrofitBuzz retrofitBuzz: Retrofit,
    @RetrofitV7 retrofitV7: Retrofit,
    searchStoreManager: SearchStoreManager,
  ): RemoteSearchRepository {
    return SearchRetrofitService(
      retrofitBuzz.create(SearchRetrofitService.AutoCompleteSearchRetrofitService::class.java),
      retrofitV7.create(SearchRetrofitService.SearchAppRetrofitService::class.java),
      searchStoreManager
    )
  }

  @Singleton
  @Provides
  fun provideLocalSearchHistoryRepository(database: SearchHistoryDatabase): SearchHistoryRepository {
    return database.searchDao()
  }

  @Singleton
  @Provides
  fun provideSearchHistoryDatabase(@ApplicationContext appContext: Context): SearchHistoryDatabase {
    return Room.databaseBuilder(appContext, SearchHistoryDatabase::class.java, "aptoide_search.db")
      .build()
  }
}