package cm.aptoide.pt.settings.domain

data class DeviceInfo(
  val sdkVersion: Int = 0,
  val screenSize: String = "",
  val esglVersion: String = "",
  val cpu: String = "",
  val densityDPI: Int = 0,
  val densityName: String = "",
) {
  override fun toString(): String = "SDK version: $sdkVersion\n" +
    "Screen size: $screenSize\n" +
    "ESGL version: $esglVersion\n" +
    "Screen code: $screenSize/$densityDPI\n" +
    "CPU: $cpu\n" +
    "Density: $densityDPI $densityName\n"
}
