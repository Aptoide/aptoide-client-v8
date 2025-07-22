package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.random.nextInt

@Singleton
class BalanceRepository @Inject constructor() {

  private val _balance = MutableStateFlow(value = 2f)
  val balance: Flow<Float> = _balance

  fun addBalance(amount: Float) = _balance.update { it + amount }
}

@HiltViewModel
class BalanceInjectionsProvider @Inject constructor(
  val balanceRepository: BalanceRepository
) : ViewModel()

@Composable
fun rememberBalance(): Pair<Float, (Float) -> Unit> = runPreviewable(
  preview = {
    Random.nextInt(2..10).toFloat() to {}
  },
  real = {
    val vm = hiltViewModel<BalanceInjectionsProvider>()
    val balance by vm.balanceRepository.balance.collectAsState(initial = 0f)
    balance to vm.balanceRepository::addBalance
  }
)
