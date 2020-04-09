package cm.aptoide.pt.editorial.epoxy

import cm.aptoide.pt.editorial.EditorialContent
import cm.aptoide.pt.editorial.EditorialDownloadEvent
import cm.aptoide.pt.themes.ThemeManager
import com.airbnb.epoxy.Typed3EpoxyController
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EditorialController(val downloadEventListener: PublishSubject<EditorialDownloadEvent>,
                          val decimalFormat: DecimalFormat,
                          val reactionsModelPresenter: ReactionsModelPresenter,
                          val themeManager: ThemeManager) :
    Typed3EpoxyController<List<EditorialContent>, Boolean, ReactionConfiguration>() {

  val bottomCardVisibilityChange = PublishSubject.create<Boolean>()

  override fun buildModels(data: List<EditorialContent>, isSingleApp: Boolean,
                           reactionConfiguration: ReactionConfiguration) {
    for (content in data) {
      EditorialContentModel_()
          .id(content.position)
          .shouldAnimate(isSingleApp)
          .editorialContent(content)
          .bottomCardVisibilityChange(bottomCardVisibilityChange)
          .decimalFormat(decimalFormat)
          .downloadEventListener(downloadEventListener)
          .addTo(this)
    }
    ReactionsContentModel_()
        .id("reactions")
        .reactionConfiguration(reactionConfiguration)
        .presenter(reactionsModelPresenter)
        .themeManager(themeManager)
        .addTo(this)
  }
}