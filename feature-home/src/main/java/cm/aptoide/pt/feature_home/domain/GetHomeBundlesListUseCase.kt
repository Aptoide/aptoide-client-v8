package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.feature_home.data.BundlesRepository
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetHomeBundlesListUseCase @Inject constructor(private val bundlesRepository: BundlesRepository) {
  fun execute(
    onStart: () -> Unit,
    onCompletion: () -> Unit,
    bypassCache: Boolean = false
  ) = bundlesRepository.getHomeBundles(bypassCache = bypassCache)
    .onStart { onStart() }
    .onCompletion { onCompletion() }
}
