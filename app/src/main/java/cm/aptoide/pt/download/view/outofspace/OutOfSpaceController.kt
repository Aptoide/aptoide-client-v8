package cm.aptoide.pt.download.view.outofspace

import com.airbnb.epoxy.TypedEpoxyController
import rx.subjects.PublishSubject

class OutOfSpaceController : TypedEpoxyController<List<InstalledApp>>() {


  val uninstallEvent: PublishSubject<String> = PublishSubject.create()

  override fun buildModels(installedApps: List<InstalledApp>) {
    for (installed in installedApps) {
      InstalledAppCardModel_().id("installed", installed.getIdentifier())
          .eventSubject(uninstallEvent)
          .application(installed)
          .addTo(this)
    }
  }

}