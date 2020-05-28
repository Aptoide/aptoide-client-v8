package com.aptoide.authentication.service

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2

interface AuthenticationService {
  suspend fun sendMagicLink(email: String): CodeAuth

  suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2
}