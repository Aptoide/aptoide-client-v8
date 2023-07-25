package cm.aptoide.pt.installer.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.installer.platform.UserActionRequest.InstallationAction

@Composable
fun UserActionDialog() {
  val viewModel = hiltViewModel<UserActionViewModel>()
  val state by viewModel.uiState.collectAsState()

  val intentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    onResult = {
      viewModel.onResult(it.resultCode == Activity.RESULT_OK)
    }
  )

  LaunchedEffect(
    key1 = state,
    block = {
      (state as? InstallationAction)?.run {
        intentLauncher.launch(intent)
      }
    }
  )
}
