package com.aptoide.android.aptoidegames.feature_payments

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.theme.AptoideTheme

class AGPaymentScreenContentProvider : PaymentScreenContentProvider {

  override val setContent: (ComponentActivity, PurchaseRequest?, onClosePayments: (Boolean) -> Unit) -> Unit =
    { context: ComponentActivity, purchaseRequest: PurchaseRequest?, onFinish: (Boolean) -> Unit ->
      context.setContent {
        val navController = rememberNavController()

        AptoideTheme(darkTheme = true) {
          NavigationGraph(
            navController = navController,
            purchaseRequest = purchaseRequest,
            onFinish = onFinish
          )
        }
      }
    }
}

@Composable
fun NavigationGraph(
  navController: NavHostController,
  purchaseRequest: PurchaseRequest?,
  onFinish: (Boolean) -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = ""
  ) {

  }
}

fun PaymentMethod<*>.getRoute(isPreSelected: Boolean = false) =
  when (this) {
    // TODO add routes to payment methods here
    else -> ""
  }
