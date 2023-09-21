package cm.aptoide.pt.feature_payment.repository.broker

import cm.aptoide.pt.feature_payment.network.BrokerApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrokerRepositoryImpl @Inject constructor(
  private val brokerApi: BrokerApi
) : BrokerRepository {

}

interface BrokerRepository {

}
