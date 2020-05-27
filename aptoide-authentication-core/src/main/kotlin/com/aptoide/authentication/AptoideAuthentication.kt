package com.aptoide.authentication

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.aptoide.authentication.network.AuthenticationService

class AptoideAuthentication(private val service: AuthenticationService) {
  suspend fun sendMagicLink(email: String): CodeAuth {
    if (email.isBlank()) {
      throw AuthenticationException("Email is blank")
    }
    return service.sendMagicLink(email)
  }

  suspend fun authenticate(magicCode: String, state: String, agent: String): OAuth2 {
    return service.authenticate(magicCode, state, agent)
  }
}
