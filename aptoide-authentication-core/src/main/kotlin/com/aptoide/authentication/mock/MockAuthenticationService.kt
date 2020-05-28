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
        false, CodeAuth.Data("TOKEN", "EMAIL"))
  }

  override suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2 {
    delay(200)
    return OAuth2("OAUTH2",
        false,
        OAuth2.Data("e05b19ewaewa7b3febf34aa5", 3000, "7605b19ewaewa7b3febf34aa5",
            "Bearer", null))
  }
}