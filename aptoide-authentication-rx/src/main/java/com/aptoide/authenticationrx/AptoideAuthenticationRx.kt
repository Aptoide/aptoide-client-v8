package com.aptoide.authenticationrx

import com.aptoide.authentication.AptoideAuthentication
import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class AptoideAuthenticationRx(private val aptoideAuthentication: AptoideAuthentication) {
  fun sendMagicLink(email: String): Single<CodeAuth> {
    return rxSingle { aptoideAuthentication.sendMagicLink(email) }
  }

  fun authenticate(magicCode: String, state: String, agent: String): Single<OAuth2> {
    return rxSingle { aptoideAuthentication.authenticate(magicCode, state, agent) }
  }
}