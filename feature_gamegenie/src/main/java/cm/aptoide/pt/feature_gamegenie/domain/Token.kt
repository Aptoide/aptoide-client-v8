package cm.aptoide.pt.feature_gamegenie.domain

import androidx.annotation.Keep
import cm.aptoide.pt.feature_gamegenie.io_models.TokenResponse

@Keep
data class Token(
  val token: String,
  val tokenType: String,
)

fun TokenResponse.toToken() = Token(this.accessToken, this.tokenType)
