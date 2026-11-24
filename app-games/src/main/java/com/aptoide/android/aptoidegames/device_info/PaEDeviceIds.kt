package com.aptoide.android.aptoidegames.device_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/** The ids Play & Earn sends as evidence, shown to testers so they can find the device. */
data class PaEDeviceIds(
  val deviceKey: String?,
  val installId: String?,
)

@HiltViewModel
class PaEDeviceIdentityInjectionsProvider @Inject constructor(
  val paEDeviceIdentity: PaEDeviceIdentity
) : ViewModel()

@Composable
fun rememberPaEDeviceIds(): PaEDeviceIds = runPreviewable(
  preview = { PaEDeviceIds(deviceKey = "device key", installId = "install id") },
  real = {
    val vm = hiltViewModel<PaEDeviceIdentityInjectionsProvider>()
    var ids by remember { mutableStateOf(PaEDeviceIds(deviceKey = null, installId = null)) }
    LaunchedEffect(Unit) {
      // A failed read shows a blank value; it must never take down the Settings screen.
      ids = PaEDeviceIds(
        deviceKey = readOrNull { vm.paEDeviceIdentity.getDeviceKey() },
        installId = readOrNull { vm.paEDeviceIdentity.getInstallId() },
      )
    }
    ids
  }
)

private suspend fun readOrNull(read: suspend () -> String?): String? =
  try {
    read()
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    null
  }
