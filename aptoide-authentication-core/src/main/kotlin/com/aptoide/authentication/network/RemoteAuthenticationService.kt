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
import retrofit2.http.GET
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
          authorizationV7.sendMagicLink(email, Type.EMAIL, arrayOf("CODE:TOKEN:EMAIL"))
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
          authorizationV7.authenticate(magicToken, Type.CODE, arrayOf("OAUTH2"), state, agent)
      val oAuth2 = authenticateResponse.body()
      if (authenticateResponse.isSuccessful && oAuth2 != null) {
        return@withContext oAuth2!!
      } else
        throw AuthenticationException(authenticateResponse.message(), authenticateResponse.code())
    }
  }

  interface AuthorizationV7 {
    @GET("user/authorize")
    suspend fun authenticate(@Query("credential") credential: String,
                             @Query("type") type: Type, @Query(
            "supported") supported: Array<String>,
                             @Query("state") state: String,
                             @Query("agent") agent: String): Response<OAuth2>

    @GET("user/authorize")
    suspend fun sendMagicLink(@Query("credential") credential: String,
                              @Query("type") type: Type, @Query(
            "supported") supported: Array<String>): Response<CodeAuth>

  }
}

enum class Type {
  EMAIL, CODE
}
