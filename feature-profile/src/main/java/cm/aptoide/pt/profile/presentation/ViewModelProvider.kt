package cm.aptoide.pt.profile.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.profile.data.model.UserProfile
import cm.aptoide.pt.profile.domain.UserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val userProfileUseCase: UserProfileUseCase,
) : ViewModel()

@Composable
fun userProfileData(key: String): Pair<UserProfile, KFunction1<UserProfile, Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: UserProfileViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = key,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserProfileViewModel(
          userProfileUseCase = injectionsProvider.userProfileUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::setUser
}
