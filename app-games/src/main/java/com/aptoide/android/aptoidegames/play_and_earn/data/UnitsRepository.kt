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
class UnitsRepository @Inject constructor(
  private val balanceRepository: BalanceRepository
) {

  private val _units = MutableStateFlow(value = 250)
  val units: Flow<Int> = _units

  fun updateUnits(exchanged: Int) {
    _units.update { it - exchanged }
    balanceRepository.addBalance(2f)
  }
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val unitsRepository: UnitsRepository
) : ViewModel()

@Composable
fun rememberAvailableUnits(): Pair<Int, (Int) -> Unit> = runPreviewable(
  preview = {
    Random.nextInt(200..300) to {}
  },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    val units by vm.unitsRepository.units.collectAsState(initial = 250)
    units to vm.unitsRepository::updateUnits
  }
)
