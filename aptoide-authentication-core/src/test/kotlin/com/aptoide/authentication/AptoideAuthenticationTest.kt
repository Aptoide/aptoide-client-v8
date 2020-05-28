package com.aptoide.authentication;

import com.aptoide.authentication.model.CodeAuth
import com.aptoide.authentication.model.OAuth2
import com.aptoide.authentication.service.AuthenticationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertNotNull

class AptoideAuthenticationTest {

  @Mock
  private lateinit var service: AuthenticationService

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
  }

  @ExperimentalCoroutinesApi
  @Test(expected = AuthenticationException::class)
  fun `magic link request with no email test`() = runBlockingTest {
    //given
    val authentication = AptoideAuthentication(service)

    //when magic link is called with empty email
    authentication.sendMagicLink("")
    //then an exception is thrown
  }

  @ExperimentalCoroutinesApi
  @Test
  fun `magic link request success test`() = runBlockingTest {
    //given
    val authentication = AptoideAuthentication(service)
    `when`(service.sendMagicLink(anyString() + "1"))
        .thenReturn(getValidCodeAuth())

    //when magic link is called with valid email
    val codeAuth = authentication.sendMagicLink("yes@wecan.usa")

    //then the auth service should be called with the email
    verify(service, times(1)).sendMagicLink("yes@wecan.usa")
    //and response should have a valid agent and state
    assertNotNull(codeAuth.agent)
    assertNotNull(codeAuth.state)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun `authenticate sucess test`() = runBlockingTest {
    //given
    val authentication = AptoideAuthentication(service)
    `when`(service.authenticate(anyString(), anyString(), anyString())).thenReturn(getValidOAuth2())

    //when authenticate is called with valid email and code
    val oAuth2 = authentication.authenticate("code", "STATE", "AGENT")

    //then an Auth response should be returned
    verify(service, times(1)).authenticate("code", "STATE", "AGENT")
    assertNotNull(oAuth2.data.accessToken)
    assertNotNull(oAuth2.data.refreshToken)

  }

  private fun getValidCodeAuth(): CodeAuth {
    return CodeAuth("CODE",
        "STATE",
        "AGENT",
        false, CodeAuth.Data("TOKEN", "EMAIL"))
  }

  private fun getValidOAuth2(): OAuth2 {
    return OAuth2("OAUTH2",
        false,
        OAuth2.Data("e05b19ewaewa7b3febf34aa5", 3000, "7605b19ewaewa7b3febf34aa5",
            "Bearer", null))
  }
}
