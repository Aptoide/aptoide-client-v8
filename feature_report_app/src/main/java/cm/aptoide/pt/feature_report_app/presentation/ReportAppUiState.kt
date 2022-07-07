package cm.aptoide.pt.feature_report_app.presentation

import cm.aptoide.pt.feature_report_app.domain.ReportApp

data class ReportAppUiState(
  val app: ReportApp,
  val reportAppOptionsList: List<ReportOption>,
  val additionalInfo: String
)