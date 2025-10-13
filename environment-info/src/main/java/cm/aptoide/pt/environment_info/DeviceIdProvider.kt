package cm.aptoide.pt.environment_info

interface DeviceIdProvider {
  suspend fun getDeviceId(): String?
}
