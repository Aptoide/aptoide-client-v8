package cm.aptoide.pt.feature_report_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_report_app.domain.ReportApp
import cm.aptoide.pt.feature_report_app.domain.usecase.ReportAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportAppViewModel @Inject constructor(reportAppUseCase: ReportAppUseCase) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    ReportAppViewModelState(
      reportApp = ReportApp("", "", "", ""),
      reportAppOptionsList = listOf(
        "Ask for update",
        "Inappropriate Content",
        "App doesn't work",
        "Fake app",
        "Virus or malware"
      )
    )
  )

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )
}

private data class ReportAppViewModelState(
  val reportApp: ReportApp,
  val reportAppOptionsList: List<String>
) {

  fun toUiState(): ReportAppUiState = ReportAppUiState(reportApp, reportAppOptionsList)
}