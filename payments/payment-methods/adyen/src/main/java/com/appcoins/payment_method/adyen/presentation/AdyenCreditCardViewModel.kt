package com.appcoins.payment_method.adyen.presentation

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
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.response.Action
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.components.model.payments.response.Threeds2Action
import com.adyen.checkout.components.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.components.model.payments.response.Threeds2FingerprintAction
import com.adyen.checkout.redirect.RedirectConfiguration
import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_method.adyen.CreditCardPaymentMethod
import com.appcoins.payment_method.adyen.CreditCardTransaction
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
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
  val paymentManager: PaymentManager,
  val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
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
          paymentMethodId = paymentMethodId,
          cardConfiguration = viewModelProvider.cardConfiguration,
          redirectConfiguration = viewModelProvider.redirectConfiguration,
          threeDS2Configuration = viewModelProvider.threeDS2Configuration,
          paymentManager = viewModelProvider.paymentManager,
          preSelectedPaymentUseCase = viewModelProvider.preSelectedPaymentUseCase
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
  private val paymentMethodId: String,
  private val paymentManager: PaymentManager,
  private val cardConfiguration: CardConfiguration,
  private val redirectConfiguration: RedirectConfiguration,
  private val threeDS2Configuration: Adyen3DS2Configuration,
  private val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
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
        val creditCardPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as CreditCardPaymentMethod

        val json = creditCardPaymentMethod.init()

        val paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(json)

        paymentMethodsApiResponse.run {
          if (storedPaymentMethods.isNullOrEmpty()) {
            viewModelState.update {
              AdyenCreditCardScreenUiState.Input(
                productInfo = creditCardPaymentMethod.productInfo,
                purchaseRequest = creditCardPaymentMethod.purchaseRequest,
                cardComponent = { activity ->
                  paymentMethods
                    ?.first { pm -> pm.type == "scheme" }
                    ?.let { CardComponent.PROVIDER.get(activity, it, cardConfiguration) }
                    ?: throw Exception()
                },
                forgetCard = null
              )
            }
          } else {
            viewModelState.update {
              AdyenCreditCardScreenUiState.Input(
                productInfo = creditCardPaymentMethod.productInfo,
                purchaseRequest = creditCardPaymentMethod.purchaseRequest,
                cardComponent = { activity ->
                  storedPaymentMethods
                    ?.first { pm -> pm.type == "scheme" }
                    ?.let { CardComponent.PROVIDER.get(activity, it, cardConfiguration, it.id) }
                    ?: throw Exception()
                },
                forgetCard = { forgetCard(creditCardPaymentMethod) }
              )
            }
          }
        }
      } catch (e: Throwable) {
        viewModelState.update { AdyenCreditCardScreenUiState.Error(e) }
      }
    }
  }

  private fun forgetCard(creditCardPaymentMethod: CreditCardPaymentMethod) {
    viewModelScope.launch {
      creditCardPaymentMethod.clearStoredCard().let {
        if (it) {
          load()
        } else {
          viewModelState.update { AdyenCreditCardScreenUiState.Error(Throwable()) }
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
      cardState.data.paymentMethod
        ?.let {
          try {
            (paymentManager.getPaymentMethod(paymentMethodId) as? CreditCardPaymentMethod)
              ?.run {
                val transaction = createTransaction(
                  paymentDetails = returnUrl to it,
                  storePaymentMethod = cardState.data.isStorePaymentMethodEnable
                )
                transaction.status.collect { status ->
                  when (status) {
                    COMPLETED,
                    -> {
                      preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(this.id)

                      viewModelState.update {
                        AdyenCreditCardScreenUiState.Success(packageName = purchaseRequest.domain)
                      }
                    }

                    PENDING_SERVICE_AUTHORIZATION -> viewModelState.update {
                      AdyenCreditCardScreenUiState.Error(
                        Exception()
                      )
                    }

                    PROCESSING -> viewModelState.update {
                      AdyenCreditCardScreenUiState.Error(
                        Exception()
                      )
                    }

                    PENDING_USER_PAYMENT -> {
                      transaction.paymentResponse?.actionJson?.let { actionJson ->
                        val action = Action.SERIALIZER.deserialize(actionJson)
                        when (action.type) {
                          REDIRECT -> {
                            val redirectAction = RedirectAction.SERIALIZER.deserialize(actionJson)
                            handleRedirection(transaction, redirectAction)
                          }

                          THREEDS2 -> {
                            val threeDs2Action = Threeds2Action.SERIALIZER.deserialize(actionJson)
                            handle3DS(transaction, threeDs2Action)
                          }

                          THREEDS2FINGERPRINT -> {
                            val threeDs2FingerPrintAction =
                              Threeds2FingerprintAction.SERIALIZER.deserialize(actionJson)
                            handle3DS(transaction, threeDs2FingerPrintAction)
                          }

                          THREEDS2CHALLENGE -> {
                            val threeDs2ChallengeAction =
                              Threeds2ChallengeAction.SERIALIZER.deserialize(actionJson)
                            handle3DS(transaction, threeDs2ChallengeAction)
                          }
                        }
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
              }
              ?: throw Exception("Failed to create transaction")
          } catch (e: Throwable) {
            viewModelState.update { AdyenCreditCardScreenUiState.Error(e) }
          }
        }
        ?: viewModelState.update {
          AdyenCreditCardScreenUiState.Error(
            IllegalArgumentException("Wrong input")
          )
        }
    }
  }

  private fun handleRedirection(
    transaction: CreditCardTransaction,
    action: RedirectAction,
  ) {
    viewModelState.update {
      AdyenCreditCardScreenUiState.Redirect(
        action = action,
        configuration = redirectConfiguration
      ) { actionData ->
        if (
          actionData.hashCode() != lastRedirectActionDataHash
          && (actionData.paymentData != null || actionData.details != null)
        ) {
          viewModelState.update { AdyenCreditCardScreenUiState.Loading }
          lastRedirectActionDataHash = actionData.hashCode()
          viewModelScope.launch {
            try {
              transaction.submitActionResponse(
                paymentData = actionData.paymentData
                  ?: action.paymentData,
                paymentDetails = actionData.details
              )
            } catch (e: Exception) {
              viewModelState.update {
                AdyenCreditCardScreenUiState.Error(e)
              }
            }
          }
        }
      }
    }
    viewModelScope.launch {
      delay(500L)
      viewModelState.update { AdyenCreditCardScreenUiState.Error(Throwable()) }
    }
  }

  private fun handle3DS(
    transaction: CreditCardTransaction,
    action: Action,
  ) {
    viewModelState.update {
      AdyenCreditCardScreenUiState.ThreeDS2(
        action = action,
        configuration = threeDS2Configuration
      ) { actionData ->
        if (
          actionData.hashCode() != last3DSActionDataHash
          && (actionData.paymentData != null || actionData.details != null)
        ) {
          viewModelState.update { AdyenCreditCardScreenUiState.Loading }
          last3DSActionDataHash = actionData.hashCode()
          viewModelScope.launch {
            try {
              transaction.submitActionResponse(
                paymentData = actionData.paymentData ?: action.paymentData,
                paymentDetails = actionData.details
              )
            } catch (e: Exception) {
              viewModelState.update {
                AdyenCreditCardScreenUiState.Error(e)
              }
            }
          }
        }
      }
    }
    viewModelScope.launch {
      delay(500L)
      viewModelState.update { AdyenCreditCardScreenUiState.Error(Throwable()) }
    }
  }
}
