package cm.aptoide.pt.feature_home.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.feature_home.data.BundlesResult
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.GetHomeBundlesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BundlesViewModel @Inject constructor(
  getHomeBundlesListUseCase: GetHomeBundlesListUseCase,
) : ViewModel() {

  val bundlesList: Flow<List<Bundle>> =
    getHomeBundlesListUseCase.execute(
      onStart = { _isLoading.value = true },
      onCompletion = { _isLoading.value = false },
      onError = { Timber.d(it) }
    ).map {
      when (it) {
        is BundlesResult.Success -> return@map it.data
        is BundlesResult.Error -> return@map emptyList()
      }
    }

  private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
  val isLoading: State<Boolean> get() = _isLoading
}
