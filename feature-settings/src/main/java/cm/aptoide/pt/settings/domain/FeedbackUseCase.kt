package cm.aptoide.pt.settings.domain

import cm.aptoide.pt.settings.data.DeviceInfoRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class FeedbackUseCase @Inject constructor(
  private val deviceInfoRepository: DeviceInfoRepository,
) {

  fun getFeedback(
    subject: String,
    description: String,
    includeLogs: Boolean,
  ): Feedback {
    val logsDeviceInfo = if (includeLogs) {
      deviceInfoRepository.createLogsFile()
    } else {
      null
    }

    return Feedback(
      destinationEmail = "games.hub@aptoide.zendesk.com",
      subject = subject,
      body = "\n\n\n\n\n$description\n",
      logs = logsDeviceInfo
    )
  }
}
