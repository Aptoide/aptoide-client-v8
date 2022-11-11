package cm.aptoide.pt.download_view.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.download_view.domain.usecase.InstallAppUseCase
import cm.aptoide.pt.feature_campaigns.CampaignsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface InstallAppUseCaseProvider {
  val installAppUseCase: InstallAppUseCase<*>
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: InstallAppUseCaseProvider,
  val installedAppOpener: InstalledAppOpener,
  val campaignsUseCase: CampaignsUseCase
) : ViewModel()

@Composable
fun keyedViewModel(key: String): DownloadViewViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DownloadViewViewModel(
          injectionsProvider.provider.installAppUseCase,
          injectionsProvider.installedAppOpener,
          injectionsProvider.campaignsUseCase,
        ) as T
      }
    }
  )
}
