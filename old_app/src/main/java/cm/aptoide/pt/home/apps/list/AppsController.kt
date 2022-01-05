package cm.aptoide.pt.home.apps.list

import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.AppClick
import cm.aptoide.pt.home.apps.list.models.DownloadCardModel_
import cm.aptoide.pt.home.apps.list.models.InstalledCardModel_
import cm.aptoide.pt.home.apps.list.models.TitleModel_
import cm.aptoide.pt.home.apps.list.models.UpdateCardModel_
import cm.aptoide.pt.home.apps.model.DownloadApp
import cm.aptoide.pt.home.apps.model.InstalledApp
import cm.aptoide.pt.home.apps.model.UpdateApp
import cm.aptoide.pt.themes.ThemeManager
import com.airbnb.epoxy.Typed3EpoxyController
import rx.subjects.PublishSubject

class AppsController(val themeManager: ThemeManager) :
    Typed3EpoxyController<List<UpdateApp>, List<InstalledApp>, List<DownloadApp>>() {

  val appEventListener: PublishSubject<AppClick> = PublishSubject.create()
  val updateAllEvent: PublishSubject<Void> = PublishSubject.create()

  override fun buildModels(updates: List<UpdateApp>, installedApps: List<InstalledApp>,
                           downloads: List<DownloadApp>) {
    TitleModel_()
        .id("downloads", "header")
        .title(R.string.apps_title_downloads_header)
        .shouldShowButton(false)
        .addIf(downloads.isNotEmpty(), this)

    for (download in downloads) {
      DownloadCardModel_()
          .id("downloads", download.identifier)
          .application(download)
          .eventSubject(appEventListener)
          .addTo(this)
    }

    // Updates
    TitleModel_()
        .id("updates", "header")
        .title(R.string.apps_title_updates_header)
        .shouldShowButton(true)
        .eventSubject(updateAllEvent)
        .addIf(updates.isNotEmpty(), this)

    for (update in updates) {
      UpdateCardModel_()
          .id("updates", update.identifier)
          .application(update)
          .eventSubject(appEventListener)
          .themeManager(themeManager)
          .addTo(this)
    }

    // Installed
    TitleModel_()
        .id("installed", "header")
        .title(R.string.apps_title_installed_apps_header)
        .shouldShowButton(false)
        .addIf(installedApps.isNotEmpty(), this)

    for (installed in installedApps) {
      InstalledCardModel_()
          .id("installed", installed.identifier)
          .application(installed)
          .addTo(this)
    }
  }


  /**
   * This is overriden so that there's named arguments instead of data1, data2, data3...
   */
  override fun setData(updates: List<UpdateApp>, installedApps: List<InstalledApp>,
                       downloads: List<DownloadApp>) {
    super.setData(updates, installedApps, downloads)
  }

}