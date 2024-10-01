package cm.aptoide.pt.feature_bonus.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object BonusData {
  private val _data =
    MutableStateFlow("Bonus" to "bonus-banner")

  val data = _data.asStateFlow()

  val currentData = _data.value

  fun setBonusData(title: String, tag: String) {
    _data.update { title to tag }
  }
}
