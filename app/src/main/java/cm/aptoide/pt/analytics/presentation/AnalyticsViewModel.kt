package cm.aptoide.pt.analytics.presentation

import androidx.lifecycle.ViewModel
import cm.aptoide.pt.analytics.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val analytics: Analytics) : ViewModel() {
  fun setUserProperties(
    storeName: String? = null,
    isDarkTheme: Boolean? = null,
  ) = analytics.setUserProperties(storeName = storeName, isDarkTheme = isDarkTheme)
}
