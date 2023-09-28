package cm.aptoide.pt.osp_handler

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface PaymentScreenContentProvider {
  val content: @Composable (NavHostController, Uri?) -> Unit
}