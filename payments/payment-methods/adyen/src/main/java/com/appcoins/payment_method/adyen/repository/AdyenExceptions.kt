package com.appcoins.payment_method.adyen.repository

open class AdyenRefusalException(message: String? = null): Exception(message)

class NoNetworkException: Exception()

class FraudException(message: String? = null): Exception(message)

class MissingBillingAddressException(message: String? = null): Exception(message)

class ConflictException(message: String? = null): Exception(message)

class NotAllowedException(message: String? = null): Exception(message)

class ForbiddenException(message: String? = null): Exception(message)

class InvalidCardException(message: String? = null): Exception(message)

class CvcLengthException(message: String? = null): Exception(message)

class CardSecurityException(message: String? = null): Exception(message)

class CurrencyNotSupportedException(message: String? = null): Exception(message)

class InvalidCountryCodeException(message: String? = null): Exception(message)

class OutdatedCardException(message: String? = null): Exception(message)

class AlreadyProcessedException(message: String? = null): Exception(message)

class PaymentErrorException(message: String? = null): Exception(message)

class PaymentNotSupportedException(message: String? = null): Exception(message)

class TransactionAmountExceededException(message: String? = null): Exception(message)

class DeclinedException (message: String? = null): AdyenRefusalException(message)
class ReferralException (message: String? = null): AdyenRefusalException(message)
class AcquirerErrorException (message: String? = null): AdyenRefusalException(message)
class BlockedCardException (message: String? = null): AdyenRefusalException(message)
class ExpiredCardException (message: String? = null): AdyenRefusalException(message)
class InvalidAmountException (message: String? = null): AdyenRefusalException(message)
class InvalidCardNumberException (message: String? = null): AdyenRefusalException(message)
class IssuerUnavailableException (message: String? = null): AdyenRefusalException(message)
class NotSupportedException (message: String? = null): AdyenRefusalException(message)
class Not3dAuthenticatedException (message: String? = null): AdyenRefusalException(message)
class NotEnoughBalanceException (message: String? = null): AdyenRefusalException(message)
class IncorrectOnlinePinException (message: String? = null): AdyenRefusalException(message)
class PinTriesExceededException (message: String? = null): AdyenRefusalException(message)

class FraudRefusalException(message: String? = null): AdyenRefusalException(message)
class CancelledDueToFraudException (message: String? = null): AdyenRefusalException(message)
class TransactionNotPermittedException (message: String? = null): AdyenRefusalException(message)
class CvcDeclinedException (message: String? = null): AdyenRefusalException(message)
class RestrictedCardException (message: String? = null): AdyenRefusalException(message)
class RevocationOfAuthException (message: String? = null): AdyenRefusalException(message)
class DeclinedNonGenericException (message: String? = null): AdyenRefusalException(message)
class WithdrawAmountExceededException (message: String? = null): AdyenRefusalException(message)
class IssuerSuspectedFraudException (message: String? = null): AdyenRefusalException(message)
