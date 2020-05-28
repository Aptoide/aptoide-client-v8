package com.aptoide.authentication.network

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.aptoide.authentication.service.AuthenticationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RemoteAuthenticationService :
    AuthenticationService {
  private val authorizationV7: AuthorizationV7 =
      Retrofit.Builder().baseUrl("https://ws2.aptoide.com/api/7/")
          .addConverterFactory(MoshiConverterFactory.create(
              Moshi.Builder().add(KotlinJsonAdapterFactory())
                  .build()))
          .build()
          .create(AuthorizationV7::class.java)

  override suspend fun sendMagicLink(email: String): CodeAuth {
    return authorizationV7.sendMagicLink(email, Type.EMAIL, arrayOf("CODE:TOKEN:EMAIL"))
  }

  override suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2 {
    return authorizationV7.authenticate(magicToken, Type.CODE, arrayOf("OAUTH2"), state, agent)
  }


  interface AuthorizationV7 {
    @GET("user/authorize")
    suspend fun authenticate(@Query("credential") credential: String,
                             @Query("type") type: Type, @Query(
            "supported") supported: Array<String>,
                             @Query("state") state: String, @Query("agent") agent: String): OAuth2

    @GET("user/authorize")
    suspend fun sendMagicLink(@Query("credential") credential: String,
                              @Query("type") type: Type, @Query(
            "supported") supported: Array<String>): CodeAuth

  }
}

enum class Type {
  EMAIL, CODE
}
