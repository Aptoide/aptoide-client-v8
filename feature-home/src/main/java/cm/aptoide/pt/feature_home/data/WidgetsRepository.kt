package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_home.domain.Widget
import kotlinx.coroutines.flow.Flow

interface WidgetsRepository {

  fun getStoreWidgets(): Flow<List<Widget>>
  fun getWidget(widgetIdentifier: String): Flow<Widget?>

}
