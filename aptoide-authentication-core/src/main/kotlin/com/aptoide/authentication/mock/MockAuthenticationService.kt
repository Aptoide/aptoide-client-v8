package com.aptoide.authentication.mock

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.aptoide.authentication.service.AuthenticationService
import kotlinx.coroutines.delay

class MockAuthenticationService :
    AuthenticationService {
  override suspend fun sendMagicLink(email: String): CodeAuth {
    delay(200)
    return CodeAuth("code",
        "estado de arte",
        "agente da pejota",
        false, CodeAuth.Data("TOKEN", "EMAIL"), "filipo@emailo.como")
  }

  override suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2 {
    delay(200)
    return OAuth2("OAUTH2",
        false,
        OAuth2.Data("accesst0k3nF4k3", 3000,
            "r3fr3shT0k3nF4k3",
            "Bearer", null))
  }
}