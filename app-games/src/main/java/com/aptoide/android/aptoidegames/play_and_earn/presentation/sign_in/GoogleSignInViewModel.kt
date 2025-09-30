package com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.play_and_earn.data.UserAccountPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val credentialManager: CredentialManager,
  private val userAuthRepository: WalletAuthRepository,
  private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : ViewModel() {
  private var googleIdTokenCredential: GoogleIdTokenCredential? = null

  private val viewModelState = MutableStateFlow<GoogleSignInUiState>(GoogleSignInUiState.Idle)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  fun signIn() {
    launchAuthentication()
  }

  private fun launchAuthentication() {
    val googleIdOption = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
      .setNonce(generateSecureRandomNonce())
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()


    viewModelScope.launch {
      try {
        val result = credentialManager.getCredential(
          context = context,
          request = request
        )

        handleAuthentication(result.credential)
      } catch (e: Throwable) {
        Timber.e("Couldn't retrieve user's credentials: ${e.message}")
      }
    }
  }

  private fun handleAuthentication(credential: Credential) {
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
      googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)

      handleAuthorization()
    } else {
      Timber.w("Credential is not of type Google ID!")
    }
  }

  private fun handleAuthorization() {
    val requestedScopes: List<Scope> = listOf(Scope(Scopes.EMAIL))

    val authorizationRequest = AuthorizationRequest.builder()
      .setRequestedScopes(requestedScopes)
      .requestOfflineAccess(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
      .build()

    viewModelScope.launch {
      viewModelState.emit(GoogleSignInUiState.HandleAuthorization(authorizationRequest))
    }
  }

  fun handleAuthorizationResult(authResult: AuthorizationResult) {
    viewModelScope.launch {
      authResult.serverAuthCode?.let {
        val result = userAuthRepository.authorizeGoogleUser(it)
        if (result.isSuccess) {
          googleIdTokenCredential?.let {
            userAccountPreferencesRepository.setUserInfo(
              UserInfo(
                name = it.displayName,
                email = result.getOrNull()?.email,
                profilePicture = it.profilePictureUri?.toString()
              )
            )
          }

          viewModelState.emit(GoogleSignInUiState.Success)
        } else {
          viewModelState.emit(GoogleSignInUiState.Error(result.exceptionOrNull()))
        }
      } ?: viewModelState.emit(GoogleSignInUiState.Error())
    }
  }

  fun handleAuthError() {
    viewModelScope.launch {
      viewModelState.emit(GoogleSignInUiState.Error())
    }
  }

  fun reset() {
    viewModelScope.launch {
      viewModelState.emit(GoogleSignInUiState.Idle)
    }
  }

  fun signOut() {
    viewModelScope.launch {
      try {
        val clearRequest = ClearCredentialStateRequest()
        credentialManager.clearCredentialState(clearRequest)
        userAccountPreferencesRepository.clearUserInfo()
        userAuthRepository.clearAuthorizationData()
      } catch (e: ClearCredentialException) {
        Timber.e("Couldn't clear user credentials: ${e.localizedMessage}")
      }
    }
  }

  private fun generateSecureRandomNonce(byteLength: Int = 32): String {
    val randomBytes = ByteArray(byteLength)
    SecureRandom.getInstanceStrong().nextBytes(randomBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
  }
}

sealed interface GoogleSignInUiState {

  object Idle : GoogleSignInUiState
  data class HandleAuthorization(val authorizationRequest: AuthorizationRequest) :
    GoogleSignInUiState

  object Success : GoogleSignInUiState
  data class Error(val e: Throwable? = null) : GoogleSignInUiState
}
