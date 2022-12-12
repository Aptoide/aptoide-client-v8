package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_home.domain.Widget
import kotlinx.coroutines.flow.Flow

interface WidgetsRepository {

  fun getStoreWidgets(): Flow<Result>
  fun getWidget(widgetIdentifier: String): Flow<Widget?>

}

sealed interface Result {
  data class Success(val data: List<Widget>) : Result
  data class Error(val e: Throwable) : Result
}
