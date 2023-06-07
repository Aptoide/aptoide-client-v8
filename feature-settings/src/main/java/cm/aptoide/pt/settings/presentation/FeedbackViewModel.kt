package cm.aptoide.pt.settings.presentation

import androidx.lifecycle.ViewModel
import cm.aptoide.pt.settings.domain.FeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
  private val feedbackUseCase: FeedbackUseCase,
) : ViewModel() {

  fun getFeedback(
    subject: String,
    description: String,
    includeLogs: Boolean,
  ) =
    feedbackUseCase.getFeedback(
      subject = subject,
      description = description,
      includeLogs = includeLogs
    )
}
