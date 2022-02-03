package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cm.aptoide.pt.feature_apps.data.AptoideWidgetsRepository
import cm.aptoide.pt.feature_apps.data.WidgetsRemoteService
import cm.aptoide.pt.feature_apps.domain.GetHomeBundlesListUseCase
import cm.aptoide.pt.feature_apps.domain.Widget
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun AppsScreen(viewModel: BundlesViewModel) {
  val bundles: List<Widget> by viewModel.bundlesList.collectAsState(initial = emptyList())
  val isLoading: Boolean by viewModel.isLoading
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .wrapContentSize(Alignment.Center)
  ) {
    if (isLoading)
      CircularProgressIndicator()
    else
      bundles.forEach {
        Text(it.title)
      }
  }
}

@Preview
@Composable
internal fun AppsScreenPreview(
  viewModel: BundlesViewModel = BundlesViewModel(
    GetHomeBundlesListUseCase(
      AptoideWidgetsRepository(
        Retrofit.Builder().baseUrl("https://ws75.aptoide.com/api/7/").client(
          OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
        )
          .addConverterFactory(
            GsonConverterFactory.create()
          ).build().create(WidgetsRemoteService::class.java)
      )
    )
  )
) {
  AppsScreen(viewModel = viewModel)
}
