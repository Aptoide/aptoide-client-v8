package com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.data.UserInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
  private val userInfoRepository: UserInfoRepository
) : ViewModel() {

  private val viewModelState = MutableStateFlow<UserInfo?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      userInfoRepository.observeUserInfo().collect { userInfo ->
        viewModelState.update { userInfo }
      }
    }
  }
}

@Composable
fun rememberUserInfo(): UserInfo? = runPreviewable(
  preview = { UserInfo() },
  real = {
    val vm = hiltViewModel<UserInfoViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
