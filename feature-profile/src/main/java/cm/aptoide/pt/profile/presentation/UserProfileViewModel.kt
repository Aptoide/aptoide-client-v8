package cm.aptoide.pt.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.profile.data.model.UserProfile
import cm.aptoide.pt.profile.domain.UserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
  private val userProfileUseCase: UserProfileUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    UserProfile(
      username = "",
      userImage = "",
      joinedData = "",
      userStore = "",
    )
  )

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      userProfileUseCase.getUser()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { user ->
          viewModelState.update { user }
        }
    }
  }

  fun createUser(user: UserProfile){
    viewModelScope.launch {
      userProfileUseCase.createUser(user)
    }
  }

  fun setUser(user: UserProfile){
    viewModelScope.launch {
      userProfileUseCase.setUser(user)
    }
  }

  fun deleteUser() {
    viewModelScope.launch {
      userProfileUseCase.deleteUser()
    }
  }
}
