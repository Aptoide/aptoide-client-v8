package com.aptoide.authentication.model

import com.squareup.moshi.Json

data class OAuth2(val type: String, val signup: Boolean,
                  val data: Data) {
  data class Data(
      @Json(name = "access_token") val accessToken: String,
      @Json(name = "expires_in") val expiresIn: Int,
      @Json(name = "refresh_token") val refreshToken: String,
      @Json(name = "token_type") val tokenType: String, val scope: String?)
}
