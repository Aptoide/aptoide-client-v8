package cm.aptoide.pt.feature_updates.domain

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class InstalledApp(
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
  val appIcon: Drawable?,
)

@Keep
data class ApkData(
  @SerializedName("signature") val signature: String,
  @SerializedName("package") val packageName: String,
  @SerializedName("vercode") val versionCode: Long,
)
