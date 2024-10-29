package cm.aptoide.pt.installer

import cm.aptoide.pt.installer.network.SplitsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicSplitsUseCase @Inject constructor(
  private val splitsRepository: SplitsRepository,
) {

  suspend fun getDynamicSplits(md5: String): List<DynamicSplit> =
    splitsRepository.getAppsDynamicSplits(md5 = md5)
}
