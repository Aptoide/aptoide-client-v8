package com.aptoide.authentication.network

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class AuthenticationService() {
  val authorizationV7: AuthorizationV7 =
      Retrofit.Builder().baseUrl("https://ws2.aptoide.com/api/7/")
          .addConverterFactory(MoshiConverterFactory.create(
              Moshi.Builder().add(KotlinJsonAdapterFactory())
                  .build()))
          .build()
          .create(AuthorizationV7::class.java)

  suspend fun sendMagicLink(email: String): CodeAuth {
//    service.sendMagicLink(email, Type.EMAIL, arrayOf("CODE:TOKEN:EMAIL"))
    delay(200)
    return CodeAuth("code",
        "estado de arte",
        "agente da pejota",
        false, CodeAuth.Data("TOKEN", "EMAIL"))
  }

  suspend fun authenticate(magicToken: String, state: String, agent: String): OAuth2 {
//    return service.authenticate(magicToken, Type.CODE, arrayOf("OAUTH2"), state, agent)
    delay(200)
    return OAuth2("OAUTH2",
        false,
        OAuth2.Data("e05b19ewaewa7b3febf34aa5", 3000, "7605b19ewaewa7b3febf34aa5",
            "Bearer", null))
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
            "supported") supported: Array<String>): OAuth2

  }
}

enum class Type {
  EMAIL, CODE
}
