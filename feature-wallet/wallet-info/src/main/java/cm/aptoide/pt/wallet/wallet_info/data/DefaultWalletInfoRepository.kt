package cm.aptoide.pt.wallet.wallet_info.data

import cm.aptoide.pt.appcoins.domain.FiatValue
import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.wallet.datastore.CurrencyPreferencesDataSource
import cm.aptoide.pt.wallet.wallet_info.data.model.WalletInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultWalletInfoRepository @Inject constructor(
  private val walletInfoApi: WalletInfoApi,
  private val currencyPreferencesDataSource: CurrencyPreferencesDataSource,
  private val dispatcher: CoroutineDispatcher
) : WalletInfoRepository {

  override suspend fun getWalletInfo(wallet: String) = withContext(dispatcher) {
    val currency = currencyPreferencesDataSource.getPreferredCurrency()
    walletInfoApi.getWalletInfo(wallet, currency).toDomainModel()
  }
}

private fun WalletInfoResponse.toDomainModel() = WalletInfo(
  wallet = wallet,
  walletBalance = FiatValue(amount = appcCreditsBalanceFiat, currency = currency, symbol = symbol),
  unitsBalance = unitsBalance,
  blocked = blocked,
  verified = verified
)
