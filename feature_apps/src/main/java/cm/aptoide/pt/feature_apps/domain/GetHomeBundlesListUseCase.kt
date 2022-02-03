package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.WidgetsRepository
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetHomeBundlesListUseCase @Inject constructor(private val widgetsRepository: WidgetsRepository) {
  fun execute(
    onStart: () -> Unit,
    onCompletion: () -> Unit,
    onError: (String) -> Unit
  ) = widgetsRepository.getStoreWidgets()
    .onStart { onStart() }
    .onCompletion { onCompletion() }
}
