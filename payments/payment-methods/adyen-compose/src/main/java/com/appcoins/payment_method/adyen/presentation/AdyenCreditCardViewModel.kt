package com.appcoins.payment_method.adyen.presentation

import android.content.res.Resources.NotFoundException
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adyen.checkout.adyen3ds2.Adyen3DS2Configuration
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardComponentState
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.ActionComponentData
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.response.Action
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.components.model.payments.response.Threeds2Action
import com.adyen.checkout.components.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.components.model.payments.response.Threeds2FingerprintAction
import com.adyen.checkout.redirect.RedirectConfiguration
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payment_method.adyen.CreditCardPaymentMethod
import com.appcoins.payment_method.adyen.CreditCardTransaction
import com.appcoins.payment_prefs.di.PaymentPrefsModule
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.COMPLETED
import com.appcoins.payments.arch.TransactionStatus.PENDING_SERVICE_AUTHORIZATION
import com.appcoins.payments.arch.TransactionStatus.PENDING_USER_PAYMENT
import com.appcoins.payments.arch.TransactionStatus.PROCESSING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val cardConfiguration: CardConfiguration,
  val redirectConfiguration: RedirectConfiguration,
  val threeDS2Configuration: Adyen3DS2Configuration,
) : ViewModel()

@Composable
fun adyenCreditCardViewModel(
  paymentMethodId: String,
): Pair<AdyenCreditCardScreenUiState, (CardComponentState, String) -> Unit> {
  val viewModelProvider = hiltViewModel<InjectionsProvider>()
  val vm: AdyenCreditCardViewModel = viewModel(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdyenCreditCardViewModel(
          paymentMethod = PaymentsModule.paymentManager.getPaymentMethod(paymentMethodId) as CreditCardPaymentMethod,
          cardConfiguration = viewModelProvider.cardConfiguration,
          redirectConfiguration = viewModelProvider.redirectConfiguration,
          threeDS2Configuration = viewModelProvider.threeDS2Configuration,
          preSelectedPaymentUseCase = PaymentPrefsModule.preSelectedPaymentUseCase,
          logger = PaymentsInitializer.logger,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::buy
}

private var lastRedirectActionDataHash: Int? = null
private var last3DSActionDataHash: Int? = null

class AdyenCreditCardViewModel(
  private val paymentMethod: CreditCardPaymentMethod,
  private val cardConfiguration: CardConfiguration,
  private val redirectConfiguration: RedirectConfiguration,
  private val threeDS2Configuration: Adyen3DS2Configuration,
  private val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
  private val logger: Logger,
) : ViewModel() {
  private companion object {
    const val REDIRECT = "redirect"
    const val THREEDS2 = "threeDS2"
    const val THREEDS2FINGERPRINT = "threeDS2Fingerprint"
    const val THREEDS2CHALLENGE = "threeDS2Challenge"
  }

  private val viewModelState =
    MutableStateFlow<AdyenCreditCardScreenUiState>(AdyenCreditCardScreenUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    load()
  }

  private fun load() {
    viewModelScope.launch {
      viewModelState.update { AdyenCreditCardScreenUiState.Loading }

      try {
        val json = paymentMethod.init()
        val response = PaymentMethodsApiResponse.SERIALIZER.deserialize(json)
        val spMethod = response.storedPaymentMethods?.first { it.type == "scheme" }
        val pMethod = response.paymentMethods?.first { it.type == "scheme" }

        val getCardComponent: (ComponentActivity) -> CardComponent = spMethod
          ?.let { pm -> { CardComponent.PROVIDER.get(it, pm, cardConfiguration, pm.id) } }
          ?: pMethod
            ?.let { pm -> { CardComponent.PROVIDER.get(it, pm, cardConfiguration) } }
          ?: throw NullPointerException("Payment method not found in response!")
        logger.logAdyenEvent(
          message = "payment_method_details",
          data = paymentMethod.toData().putResult(true),
        )

        viewModelState.update {
          AdyenCreditCardScreenUiState.Input(
            productInfo = paymentMethod.productInfo,
            purchaseRequest = paymentMethod.purchaseRequest,
            cardComponent = getCardComponent,
            forgetCard = spMethod?.let { ::forgetCard }
          )
        }
      } catch (e: Throwable) {
        logger.logAdyenEvent(
          message = "payment_method_details",
          data = emptyMap<String, Any?>().putResult(false),
        )
        logger.logAdyenError(e)
        viewModelState.update { AdyenCreditCardScreenUiState.Error(e) }
      }
    }
  }

  private fun forgetCard() {
    viewModelScope.launch {
      paymentMethod.clearStoredCard().let {
        logger.logAdyenEvent(
          message = "clear_stored_payment_details",
          data = paymentMethod.toData().putResult(it),
        )
        if (it) {
          load()
        } else {
          val exception = IllegalStateException("Failed to clear stored card!")
          logger.logAdyenError(exception)
          viewModelState.update { AdyenCreditCardScreenUiState.Error(exception) }
        }
      }
    }
  }

  fun buy(
    cardState: CardComponentState,
    returnUrl: String,
  ) {
    viewModelScope.launch {
      viewModelState.update { AdyenCreditCardScreenUiState.MakingPurchase }
      cardState.data.paymentMethod?.let {
        try {
          val transaction = paymentMethod.createTransaction(
            paymentDetails = returnUrl to it,
            storePaymentMethod = cardState.data.isStorePaymentMethodEnable
          )
          logger.logAdyenEvent(
            message = "transaction_create",
            data = paymentMethod.toData().putTransaction(transaction).putResult(true),
          )
          transaction.status.collect { status ->
            logger.logAdyenEvent(
              message = "transaction_update",
              data = paymentMethod.toData().putTransaction(transaction)
                .putTransactionStatus(status),
            )
            when (status) {
              COMPLETED -> {
                preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(paymentMethod.id)
                viewModelState.update {
                  AdyenCreditCardScreenUiState.Success(paymentMethod.purchaseRequest.domain)
                }
              }

              PENDING_SERVICE_AUTHORIZATION,
              PROCESSING,
              -> viewModelState.update {
                val error = java.lang.IllegalStateException("Unexpected tx state: $status")
                logger.logAdyenError(error)
                AdyenCreditCardScreenUiState.Error(error)
              }

              PENDING_USER_PAYMENT -> {
                transaction.paymentResponse?.action?.let { actionJson ->
                  val action = Action.SERIALIZER.deserialize(actionJson)
                  val onSubmit: (actionData: ActionComponentData) -> Unit = {
                    submitUserAction(transaction, action, it)
                  }
                  when (action.type) {
                    REDIRECT -> {
                      val redirectAction = RedirectAction.SERIALIZER.deserialize(actionJson)
                      handleRedirection(redirectAction, onSubmit)
                    }

                    THREEDS2 -> {
                      val threeDs2Action = Threeds2Action.SERIALIZER.deserialize(actionJson)
                      handle3DS(threeDs2Action, onSubmit)
                    }

                    THREEDS2FINGERPRINT -> {
                      val threeDs2FingerPrintAction =
                        Threeds2FingerprintAction.SERIALIZER.deserialize(actionJson)
                      handle3DS(threeDs2FingerPrintAction, onSubmit)
                    }

                    THREEDS2CHALLENGE -> {
                      val threeDs2ChallengeAction =
                        Threeds2ChallengeAction.SERIALIZER.deserialize(actionJson)
                      handle3DS(threeDs2ChallengeAction, onSubmit)
                    }

                    else -> null
                  }
                }?.also { state ->
                  viewModelScope.launch {
                    delay(500L)
                    viewModelState.update { AdyenCreditCardScreenUiState.Error(Throwable()) }
                  }
                  viewModelState.update { state }
                  logger.logAdyenEvent(
                    message = "3ds_start",
                    data = paymentMethod.toData().putTransaction(transaction).putResult(true),
                  )
                } ?: {
                  logger.logAdyenEvent(
                    message = "3ds_start",
                    data = paymentMethod.toData().putTransaction(transaction).putResult(false),
                  )
                  val error = NullPointerException("No action for pending user payment state")
                  logger.logAdyenError(error)
                  viewModelState.update { AdyenCreditCardScreenUiState.Error(error) }
                }
              }

              TransactionStatus.FAILED,
              TransactionStatus.CANCELED,
              TransactionStatus.INVALID_TRANSACTION,
              TransactionStatus.FRAUD,
              -> viewModelState.update { AdyenCreditCardScreenUiState.Error(Exception()) }

              else -> Unit
            }
          }
        } catch (e: Throwable) {
          logger.logAdyenEvent(
            message = "transaction_create",
            data = emptyMap<String, Any?>().putResult(false),
          )
          logger.logAdyenError(e)
          viewModelState.update { AdyenCreditCardScreenUiState.Error(e) }
        }
      }
        ?: viewModelState.update {
          logger.logAdyenError(NotFoundException("Credit card input is missing!"))
          AdyenCreditCardScreenUiState.Error(
            IllegalArgumentException("Wrong input")
          )
        }
    }
  }

  private fun handleRedirection(
    action: RedirectAction,
    onSubmit: (actionData: ActionComponentData) -> Unit,
  ) = AdyenCreditCardScreenUiState.Redirect(
    action = action,
    configuration = redirectConfiguration
  ) { actionData ->
    if (
      actionData.hashCode() != lastRedirectActionDataHash
      && (actionData.paymentData != null || actionData.details != null)
    ) {
      lastRedirectActionDataHash = actionData.hashCode()
      onSubmit(actionData)
    }
  }

  private fun handle3DS(
    action: Action,
    onSubmit: (actionData: ActionComponentData) -> Unit,
  ) = AdyenCreditCardScreenUiState.ThreeDS2(
    action = action,
    configuration = threeDS2Configuration
  ) { actionData ->
    if (
      actionData.hashCode() != last3DSActionDataHash
      && (actionData.paymentData != null || actionData.details != null)
    ) {
      last3DSActionDataHash = actionData.hashCode()
      onSubmit(actionData)
    }
  }

  private fun submitUserAction(
    transaction: CreditCardTransaction,
    action: Action,
    actionData: ActionComponentData,
  ) {
    viewModelState.update { AdyenCreditCardScreenUiState.Loading }
    viewModelScope.launch {
      try {
        transaction.submitActionResponse(
          paymentData = actionData.paymentData ?: action.paymentData,
          paymentDetails = actionData.details
        )
        logger.logAdyenEvent(
          message = "transaction_submit_3ds",
          data = paymentMethod.toData()
            .putTransaction(transaction)
            .putResult(true),
        )
      } catch (e: Exception) {
        logger.logAdyenEvent(
          message = "transaction_submit_3ds",
          data = paymentMethod.toData()
            .putTransaction(transaction)
            .putResult(false),
        )
        logger.logAdyenError(e)
        viewModelState.update {
          AdyenCreditCardScreenUiState.Error(e)
        }
      }
    }
  }
}

private fun Logger.logAdyenError(throwable: Throwable) = logError(
  tag = "adyen",
  throwable = throwable,
)

private fun Logger.logAdyenEvent(
  message: String,
  data: Map<String, Any?> = emptyMap(),
) = logEvent(
  tag = "adyen",
  message = message,
  data = data
)

private fun <T> PaymentMethod<T>.toData(): Map<String, Any?> = mapOf(
  "payment_method" to id,
  "product_info" to mapOf(
    "sku" to productInfo.sku,
    "value" to productInfo.priceValue,
    "currency" to productInfo.priceCurrency,
  ),
  "wallet" to wallet,
)

private fun Map<String, Any?>.putTransaction(transaction: CreditCardTransaction) =
  this + mapOf("txId" to transaction.uid)

private fun Map<String, Any?>.putTransactionStatus(status: TransactionStatus) =
  this + mapOf("status" to status)

private fun Map<String, Any?>.putResult(success: Boolean) =
  this + mapOf("result" to if (success) "success" else "fail")
