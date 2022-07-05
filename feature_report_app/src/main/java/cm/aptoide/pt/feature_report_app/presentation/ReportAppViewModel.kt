package cm.aptoide.pt.feature_report_app.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_report_app.domain.ReportApp
import cm.aptoide.pt.feature_report_app.domain.usecase.ReportAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReportAppViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val reportAppUseCase: ReportAppUseCase
) : ViewModel() {

  private val appName: String? = savedStateHandle.get("appName")
  private val appIcon: String? = savedStateHandle.get("appIcon")
  private val versionName: String? = savedStateHandle.get("versionName")
  private val malwareRank: String? = savedStateHandle.get("malwareRank")

  private val viewModelState = MutableStateFlow(
    ReportAppViewModelState(
      reportApp = ReportApp(appName, appIcon, versionName, malwareRank),
      reportAppOptionsList = arrayListOf(
        ReportOption("Ask for update", false),
        ReportOption("Inappropriate Content", false),
        ReportOption("App doesn't work", false),
        ReportOption("Fake app", false),
        ReportOption("Virus or malware", false)
      ), additionalInfo = ""
    )
  )

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun submitReport() {
    // reportAppUseCase.reportApp()
  }

  fun onAdditionalInfoChanged(additionalInfo: String) {
    viewModelState.update { it.copy(additionalInfo = additionalInfo) }
  }

  fun onSelectReportOption(reportOption: ReportOption) {
    viewModelState.update {
      val newList = it.reportAppOptionsList.map { option ->
        if (reportOption == option) {
          option.copy(isSelected = true)
        } else {
          option.copy(isSelected = false)
        }
      }.toList()
      it.copy(reportAppOptionsList = newList)
    }
  }
}

private data class ReportAppViewModelState(
  val reportApp: ReportApp,
  val reportAppOptionsList: List<ReportOption>, val additionalInfo: String
) {

  fun toUiState(): ReportAppUiState =
    ReportAppUiState(reportApp, reportAppOptionsList, additionalInfo)
}