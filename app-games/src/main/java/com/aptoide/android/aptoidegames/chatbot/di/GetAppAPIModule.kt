package com.aptoide.android.aptoidegames.chatbot.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import com.aptoide.android.aptoidegames.chatbot.data.GetAppApiRepository
import com.aptoide.android.aptoidegames.chatbot.data.GetAppApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GetAppAPIModule {

    @Provides
    @Singleton
    fun providesGetAppRepository(
        @RetrofitV7 retrofitV7: Retrofit,
    ): GetAppApiRepository = GetAppApiRepositoryImpl(
        appsRemoteDataSource = retrofitV7.create(GetAppApiRepositoryImpl.Retrofit::class.java),
        scope = CoroutineScope(Dispatchers.IO)
    )
}