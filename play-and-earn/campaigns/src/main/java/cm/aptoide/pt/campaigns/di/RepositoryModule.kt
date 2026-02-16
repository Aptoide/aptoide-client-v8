package cm.aptoide.pt.campaigns.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import cm.aptoide.pt.campaigns.data.DefaultPaECampaignsRepository
import cm.aptoide.pt.campaigns.data.DefaultPaEMissionsRepository
import cm.aptoide.pt.campaigns.data.PaECampaignsApi
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.data.database.PaECampaignsDatabase
import cm.aptoide.pt.wallet.authorization.data.WalletAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providePaECampaignsApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain rewardsDomain: String,
    walletAuthInterceptor: WalletAuthInterceptor
  ): PaECampaignsApi {
    val client = okHttpClient
      .newBuilder()
      .addInterceptor(walletAuthInterceptor)
      .build()

    return Retrofit.Builder()
      .client(client)
      .baseUrl(rewardsDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PaECampaignsApi::class.java)
  }

  @Provides
  @Singleton
  fun providePaECampaignsRepository(
    paeCampaignsApi: PaECampaignsApi,
    paECampaignsDatabase: PaECampaignsDatabase
  ): PaECampaignsRepository = DefaultPaECampaignsRepository(
    paeCampaignsApi = paeCampaignsApi,
    paEAppsDao = paECampaignsDatabase.paeAppsDao(),
    dispatcher = Dispatchers.IO
  )

  @Provides
  @Singleton
  fun providePaEMissionsRepository(
    paeCampaignsApi: PaECampaignsApi,
    paECampaignsDatabase: PaECampaignsDatabase
  ): PaEMissionsRepository = DefaultPaEMissionsRepository(
    paeCampaignsApi = paeCampaignsApi,
    paeMissionDao = paECampaignsDatabase.paeMissionDao(),
    dispatcher = Dispatchers.IO
  )

  @Singleton
  @Provides
  fun providePaECampaignsDatabase(@ApplicationContext appContext: Context): PaECampaignsDatabase {
    return Room.databaseBuilder(appContext, PaECampaignsDatabase::class.java, "pae_campaigns.db")
      .build()
  }
}
