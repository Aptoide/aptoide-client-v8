package cm.aptoide.pt.autoupdate

import rx.Single

class AutoUpdateRepository(private val autoUpdateService: AutoUpdateService) {

  private lateinit var autoUpdateModel: AutoUpdateModel

  fun loadFreshAutoUpdateModel(): Single<AutoUpdateModel> {
    return loadAndSaveAutoUpdateModel()
  }

  fun loadAutoUpdateModel(): Single<AutoUpdateModel> {
    return if (::autoUpdateModel.isInitialized) {
      Single.just(autoUpdateModel)
    } else
      loadAndSaveAutoUpdateModel()

  }

  private fun loadAndSaveAutoUpdateModel(): Single<AutoUpdateModel> {
    return autoUpdateService.loadAutoUpdateModel()
        .doOnSuccess { autoUpdateModel = it }
  }
}