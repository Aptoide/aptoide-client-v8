package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionStartRequestData(
  @SerializedName("package") val packageName: String,
  @SerializedName("device_id") val deviceId: String
)
