package com.appcoins.payment_method.adyen.presentation

import android.app.Activity
import android.content.res.Resources.NotFoundException
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardComponentState
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.response.Action
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.components.model.payments.response.Threeds2Action
import com.adyen.checkout.components.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.components.model.payments.response.Threeds2FingerprintAction
import com.appcoins.payment_method.adyen.CreditCardPaymentMethod
import com.appcoins.payment_method.adyen.CreditCardTransaction
import com.appcoins.payment_method.adyen.presentation.ActionResolution.Cancel
import com.appcoins.payment_method.adyen.presentation.ActionResolution.Fail
import com.appcoins.payment_method.adyen.presentation.ActionResolution.Success
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.COMPLETED
import com.appcoins.payments.arch.TransactionStatus.PENDING_SERVICE_AUTHORIZATION
import com.appcoins.payments.arch.TransactionStatus.PENDING_USER_PAYMENT
import com.appcoins.payments.arch.TransactionStatus.PROCESSING
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.CancellationException

class AdyenCreditCardViewModel(
  private val paymentMethod: CreditCardPaymentMethod,
  private val cardConfiguration: CardConfiguration,
  private val logger: Logger,
) : ViewModel() {
  private companion object {
    const val REDIRECT = "redirect"
    const val THREEDS2 = "threeDS2"
    const val THREEDS2FINGERPRINT = "threeDS2Fingerprint"
    const val THREEDS2CHALLENGE = "threeDS2Challenge"
  }

  private val viewModelState =
    MutableStateFlow<AdyenCreditCardUiState>(AdyenCreditCardUiState.Loading)

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
      viewModelState.update { AdyenCreditCardUiState.Loading }

      try {
        val json = paymentMethod.init()
        val response = PaymentMethodsApiResponse.SERIALIZER.deserialize(json)
        val spMethod = response.storedPaymentMethods?.first { it.type == "scheme" }
        val pMethod = response.paymentMethods?.first { it.type == "scheme" }

        val getCardComponent: (ComponentActivity) -> CardComponent = spMethod
          ?.let { pm ->
            {
              CardComponent.PROVIDER.get(
                it,
                pm,
                cardConfiguration,
                getAdyenComponentStoredVMKey<CardComponent>()
              )
            }
          }
          ?: pMethod
            ?.let { pm -> { CardComponent.PROVIDER.get(it, pm, cardConfiguration) } }
          ?: throw NullPointerException("Payment method not found in response!")
        logger.logAdyenEvent(
          message = "payment_method_details",
          data = paymentMethod.toData().putResult(true),
        )

        viewModelState.update {
          AdyenCreditCardUiState.Input(
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
        viewModelState.update { AdyenCreditCardUiState.Error(e) }
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
          viewModelState.update { AdyenCreditCardUiState.Error(exception) }
        }
      }
    }
  }

  fun buy(cardState: CardComponentState) {
    viewModelScope.launch {
      viewModelState.update { AdyenCreditCardUiState.MakingPurchase }
      cardState.data.paymentMethod?.let {
        try {
          val transaction = paymentMethod.createTransaction(
            paymentDetails = it to cardState.data.isStorePaymentMethodEnable
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
                viewModelState.update {
                  AdyenCreditCardUiState.Success(paymentMethod.purchaseRequest.domain)
                }
              }

              PENDING_SERVICE_AUTHORIZATION,
              PROCESSING,
              -> viewModelState.update {
                val error = java.lang.IllegalStateException("Unexpected tx state: $status")
                logger.logAdyenError(error)
                AdyenCreditCardUiState.Error(error)
              }

              PENDING_USER_PAYMENT -> {
                transaction.paymentResponse?.action?.let { actionJson ->
                  val action = Action.SERIALIZER.deserialize(actionJson)
                  val onSubmit = { resolution: ActionResolution ->
                    submitUserAction(transaction, action, resolution)
                  }
                  when (action.type) {
                    REDIRECT -> AdyenCreditCardUiState.UserAction(
                      resolveWith = UserActionResolver(
                        action = RedirectAction.SERIALIZER.deserialize(actionJson),
                        resolverActivity = AdyenRedirectActivity::class.java,
                        submitActionResult = onSubmit
                      )::resolveWith
                    )

                    THREEDS2 -> AdyenCreditCardUiState.UserAction(
                      resolveWith = UserActionResolver(
                        action = Threeds2Action.SERIALIZER.deserialize(actionJson),
                        resolverActivity = Adyen3DS2Activity::class.java,
                        submitActionResult = onSubmit
                      )::resolveWith
                    )

                    THREEDS2FINGERPRINT -> AdyenCreditCardUiState.UserAction(
                      resolveWith = UserActionResolver(
                        action = Threeds2FingerprintAction.SERIALIZER.deserialize(actionJson),
                        resolverActivity = Adyen3DS2Activity::class.java,
                        submitActionResult = onSubmit
                      )::resolveWith
                    )

                    THREEDS2CHALLENGE -> AdyenCreditCardUiState.UserAction(
                      resolveWith = UserActionResolver(
                        action = Threeds2ChallengeAction.SERIALIZER.deserialize(actionJson),
                        resolverActivity = Adyen3DS2Activity::class.java,
                        submitActionResult = onSubmit
                      )::resolveWith
                    )

                    else -> null
                  }
                }?.also { state ->
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
                  viewModelState.update { AdyenCreditCardUiState.Error(error) }
                }
              }

              TransactionStatus.FAILED,
              TransactionStatus.CANCELED,
              TransactionStatus.INVALID_TRANSACTION,
              TransactionStatus.FRAUD,
              -> viewModelState.update { AdyenCreditCardUiState.Error(Exception()) }

              else -> Unit
            }
          }
        } catch (e: Throwable) {
          logger.logAdyenEvent(
            message = "transaction_create",
            data = emptyMap<String, Any?>().putResult(false),
          )
          logger.logAdyenError(e)
          viewModelState.update { AdyenCreditCardUiState.Error(e) }
        }
      }
        ?: viewModelState.update {
          logger.logAdyenError(NotFoundException("Credit card input is missing!"))
          AdyenCreditCardUiState.Error(
            IllegalArgumentException("Wrong input")
          )
        }
    }
  }

  fun clearAdyenComponents(activity: ComponentActivity) {
    ViewModelProvider(activity).run {
      listOf(
        getAdyenComponentVMKey<CardComponent>(),
        getAdyenComponentStoredVMKey<CardComponent>(),
      ).forEach {
        try {
          //Invalidates current Adyen ViewModel, since the provided modelClass is not Adyen component
          get(key = it, modelClass = (object : ViewModel() {})::class.java)
          activity.savedStateRegistry.unregisterSavedStateProvider(it)
          logger.logAdyenEvent(
            message = "clear_adyen_component",
            data = mapOf("name" to it).putResult(true),
          )
        } catch (e: Throwable) {
          logger.logAdyenEvent(
            message = "clear_adyen_component",
            data = emptyMap<String, Any?>().putResult(false),
          )
          logger.logAdyenError(e)
        }
      }
    }
  }

  private inline fun <reified T : Any> getAdyenComponentVMKey() =
    "androidx.lifecycle.ViewModelProvider.DefaultKey:${T::class.java.canonicalName}"

  private inline fun <reified T : Any> getAdyenComponentStoredVMKey() =
    "androidx.lifecycle.ViewModelProvider.DefaultKey:${T::class.java.canonicalName}/stored"

  private fun submitUserAction(
    transaction: CreditCardTransaction,
    action: Action,
    actionResolution: ActionResolution,
  ) {
    when (actionResolution) {
      Cancel -> viewModelState.update { AdyenCreditCardUiState.Error(CancellationException()) }
      Fail -> viewModelState.update { AdyenCreditCardUiState.Error(Exception()) }

      is Success -> {
        viewModelState.update { AdyenCreditCardUiState.Loading }
        viewModelScope.launch {
          try {
            transaction.submitActionResponse(
              paymentData = actionResolution.data.paymentData ?: action.paymentData,
              paymentDetails = actionResolution.data.details
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
              AdyenCreditCardUiState.Error(e)
            }
          }
        }
      }
    }
  }
}

private class UserActionResolver<T : Parcelable>(
  private val action: T,
  private val resolverActivity: Class<out Activity>,
  private val submitActionResult: (ActionResolution) -> Unit,
) {
  private var launcher: ActivityResultLauncher<T>? = null
  private val key = UUID.randomUUID().toString()

  fun resolveWith(activityRegistry: ActivityResultRegistry) {
    if (launcher != null) throw IllegalStateException("Resolver launcher is already registered")
    launcher = activityRegistry.register(key, AdyenActionResolveContract<T>(resolverActivity)) {
      submitActionResult(it)
      finish()
    }.apply {
      launch(action)
    }
  }

  private fun finish() {
    launcher?.unregister()
    launcher = null
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
