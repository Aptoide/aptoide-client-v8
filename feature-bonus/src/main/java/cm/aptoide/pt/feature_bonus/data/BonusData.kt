package cm.aptoide.pt.feature_bonus.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object BonusData {
  private val _data =
    MutableStateFlow("bonus-banner-more" to "listApps/store_id=3613731/group_id=15614123/order=rand")

  val data = _data.asStateFlow()

  val currentData = _data.value

  fun setBonusData(title: String, tag: String) {
    _data.update { title to tag }
  }
}
