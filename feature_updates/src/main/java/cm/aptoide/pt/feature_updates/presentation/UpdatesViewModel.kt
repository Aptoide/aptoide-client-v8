package cm.aptoide.pt.feature_updates.presentation

import androidx.lifecycle.ViewModel
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(private val getInstalledAppsUseCase: GetInstalledAppsUseCase) :
  ViewModel()