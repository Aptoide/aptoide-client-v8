package com.aptoide.android.aptoidegames.feature_payments

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.extensions.staticComposable
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.methods.paypal.PaypalPaymentMethod
import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.feature_payments.payment_methods.paymentsRoute
import com.aptoide.android.aptoidegames.feature_payments.payment_methods.paymentsScreen
import com.aptoide.android.aptoidegames.feature_payments.paypal.buildPaypalRoute
import com.aptoide.android.aptoidegames.feature_payments.paypal.paypalPaymentScreen
import com.aptoide.android.aptoidegames.feature_payments.wallet.paymentsWalletInstallationScreen
import com.aptoide.android.aptoidegames.feature_payments.wallet.paymentsWalletInstalledScreen
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
    startDestination = paymentsRoute
  ) {
    staticComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = paymentsScreen(
        onFinish = onFinish,
        purchaseRequest = purchaseRequest,
      )
    )

    staticComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = paypalPaymentScreen(onFinish = onFinish)
    )

    staticComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = paymentsWalletInstallationScreen(
        onFinish = onFinish,
        purchaseRequest = purchaseRequest,
      )
    )

    staticComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = paymentsWalletInstalledScreen(
        purchaseRequest = purchaseRequest,
        onFinish = onFinish,
      )
    )

  }
}

fun PaymentMethod<*>.getRoute(isPreSelected: Boolean = false) =
  when (this) {
    // TODO add routes to payment methods here
    is PaypalPaymentMethod -> buildPaypalRoute(this.id, isPreSelected)
    else -> ""
  }
