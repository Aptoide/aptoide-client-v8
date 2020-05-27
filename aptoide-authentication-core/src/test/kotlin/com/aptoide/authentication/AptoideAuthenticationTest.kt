package com.aptoide.authentication;

import org.junit.Test

class AptoideAuthenticationTest {

  @Test
  fun testMagicLinkWithNoEmail() {
    //given a fresh Authentication

    //when magic link is called with empty email
    //then an exception is thrown

  }

  @Test
  fun testMagicLinkSuccess() {
    //given a fresh Authentication

    //when magic link is called with valid email
    //then a status ok should be returned

  }

  @Test
  fun testAuthenticate() {
    //given a fresh Authentication

    //when authenticate is called with valid email and code
    //then an Auth response should be returned

  }

  @Test
  fun testAuthenticateWithRefresh() {
    //given a fresh Authentication

    //when authenticate is called with valid refresh token
    //then an Auth response should be returned

  }
}