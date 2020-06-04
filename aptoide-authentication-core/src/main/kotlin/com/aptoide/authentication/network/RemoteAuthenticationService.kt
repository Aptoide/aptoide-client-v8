package com.aptoide.authentication.network

import com.aptoide.authentication.AuthenticationException
import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.aptoide.authentication.service.AuthenticationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

class RemoteAuthenticationService :
    AuthenticationService {
  private val authorizationV7: AuthorizationV7 =
      Retrofit.Builder().baseUrl("https://webservices-devel.aptoide.com/api/7/")
          .addConverterFactory(MoshiConverterFactory.create(
              Moshi.Builder().add(KotlinJsonAdapterFactory())
                  .build()))
          .build()
          .create(AuthorizationV7::class.java)

  override suspend fun sendMagicLink(email: String): CodeAuth {
    return withContext(Dispatchers.IO) {
      val sendMagicLinkResponse =
          authorizationV7.sendMagicLink(Type.EMAIL,
              Credentials(email, arrayOf("TOS", "PRIVACY", "DISTRIBUTION"),
                  arrayOf("CODE:TOKEN:EMAIL")))
      val codeAuth = sendMagicLinkResponse.body()
      if (sendMagicLinkResponse.isSuccessful && codeAuth != null) {
        codeAuth.email = email
        return@withContext codeAuth!!
      } else {
        throw AuthenticationException(sendMagicLinkResponse.message(), sendMagicLinkResponse.code())
      }
    }
  }

  override suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2 {
    return withContext(Dispatchers.IO) {
      val authenticateResponse =
          authorizationV7.authenticate(Type.CODE, state, agent,
              Credentials(magicToken, arrayOf("TOS", "PRIVACY", "DISTRIBUTION"), arrayOf("OAUTH2")))
      val oAuth2 = authenticateResponse.body()
      if (authenticateResponse.isSuccessful && oAuth2 != null) {
        return@withContext oAuth2!!
      } else
        throw AuthenticationException(authenticateResponse.message(), authenticateResponse.code())
    }
  }

  interface AuthorizationV7 {
    @POST("user/authorize")
    suspend fun authenticate(@Query("type") type: Type,
                             @Query("state") state: String,
                             @Query("agent") agent: String,
                             @Body credentials: Credentials): Response<OAuth2>

    @POST("user/authorize")
    suspend fun sendMagicLink(@Query("type") type: Type,
                              @Body credentials: Credentials): Response<CodeAuth>

  }

  data class Credentials(val credential: String, val accepted: Array<String>,
                         val supported: Array<String>)
}

enum class Type {
  EMAIL, CODE
}
