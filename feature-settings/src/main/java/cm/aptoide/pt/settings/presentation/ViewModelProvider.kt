package cm.aptoide.pt.settings.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.settings.domain.AppUpdatesPreferencesUseCase
import cm.aptoide.pt.settings.domain.AutoUpdatePreferencesUseCase
import cm.aptoide.pt.settings.domain.BetaVersionsPreferencesUseCase
import cm.aptoide.pt.settings.domain.CacheSizePreferencesUseCase
import cm.aptoide.pt.settings.domain.CampaignsPreferencesUseCase
import cm.aptoide.pt.settings.domain.CompatibleAppsPreferencesUseCase
import cm.aptoide.pt.settings.domain.DownloadOverWifiPreferencesUseCase
import cm.aptoide.pt.settings.domain.NativeInstallerPreferencesUseCase
import cm.aptoide.pt.settings.domain.RootInstallationPreferencesUseCase
import cm.aptoide.pt.settings.domain.ShowAdultContentPreferencesUseCase
import cm.aptoide.pt.settings.domain.SystemAppsPreferencesUseCase
import cm.aptoide.pt.settings.domain.ThemePreferencesUseCase
import cm.aptoide.pt.settings.domain.UpdateAptoidePreferencesUseCase
import cm.aptoide.pt.settings.domain.UserPinPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val themePreferencesUseCase: ThemePreferencesUseCase,

  // GENERAL SECTION
  val compatibleAppsPreferencesUseCase: CompatibleAppsPreferencesUseCase,
  val downloadOverWifiPreferencesUseCase: DownloadOverWifiPreferencesUseCase,
  val betaVersionsPreferencesUseCase: BetaVersionsPreferencesUseCase,
  val nativeInstallerPreferencesUseCase: NativeInstallerPreferencesUseCase,

  // UPDATES SECTION
  val systemAppsPreferencesUseCase: SystemAppsPreferencesUseCase,

  // NOTIFICATIONS SECTION
  val campaignsPreferencesUseCase: CampaignsPreferencesUseCase,
  val appUpdatesPreferencesUseCase: AppUpdatesPreferencesUseCase,
  val updateAptoidePreferencesUseCase: UpdateAptoidePreferencesUseCase,

  // STORAGE SECTION
  val cacheSizePreferencesUseCase: CacheSizePreferencesUseCase,

  // ADULT CONTENT SECTION
  val adultContentPreferencesUseCase: ShowAdultContentPreferencesUseCase,
  val userPinPreferencesUseCase: UserPinPreferencesUseCase,

  // ROOT SECTION
  val rootInstallationPreferencesUseCase: RootInstallationPreferencesUseCase,
  val autoUpdatePreferencesUseCase: AutoUpdatePreferencesUseCase,
) : ViewModel()

@Composable
fun themePreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.themePreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

// GENERAL SECTION
@Composable
fun compatibleAppsPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.compatibleAppsPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun downloadOverWifiPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.downloadOverWifiPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun betaVersionsPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.betaVersionsPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun nativeInstallerPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.nativeInstallerPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

// UPDATES SECTION
@Composable
fun systemAppsPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.systemAppsPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

// NOTIFICATIONS SECTION
@Composable
fun campaignsPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.campaignsPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun appUpdatesPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.appUpdatesPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun updateAptoidePreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.updateAptoidePreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

// STORAGE SECTION
@Composable
fun cacheSizePreferences(key: String): Pair<Int, KFunction1<Int, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: IntViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return IntViewModel(
          intPreferencesUseCase = injectionsProvider.cacheSizePreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setValue
}

// ADULT CONTENT SECTION
@Composable
fun adultContentPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.adultContentPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun userPinPreferences(key: String): Pair<String, KFunction1<String, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: StringViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StringViewModel(
          stringPreferencesUseCase = injectionsProvider.userPinPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setString
}

// ROOT SECTION
@Composable
fun rootInstallationPreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.rootInstallationPreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}

@Composable
fun autoUpdatePreferences(key: String): Pair<Boolean?, KFunction1<Boolean?, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: FlagViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlagViewModel(
          flagPreferencesUseCase = injectionsProvider.autoUpdatePreferencesUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setFlag
}
