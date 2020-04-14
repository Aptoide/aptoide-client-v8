package cm.aptoide.pt.editorial.epoxy

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.editorial.EditorialContent
import cm.aptoide.pt.editorial.EditorialDownloadEvent
import cm.aptoide.pt.editorial.epoxy.comments.ChangeFilterEvent
import cm.aptoide.pt.editorial.epoxy.comments.CommentGroupModel
import cm.aptoide.pt.editorial.epoxy.comments.CommentsTitleModel_
import cm.aptoide.pt.editorial.epoxy.comments.LoadingViewModel_
import cm.aptoide.pt.themes.ThemeManager
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.Typed4EpoxyController
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import java.util.*

class EditorialController(val downloadEventListener: PublishSubject<EditorialDownloadEvent>,
                          val decimalFormat: DecimalFormat,
                          val reactionsModelPresenter: ReactionsModelPresenter,
                          val themeManager: ThemeManager,
                          val dateUtils: AptoideUtils.DateTimeU,
                          val commentsTitle: String) :
    Typed4EpoxyController<List<EditorialContent>, Boolean, ReactionConfiguration, CommentsResponseModel>() {

  val bottomCardVisibilityChange = PublishSubject.create<Boolean>()
  val filterChangedEventSubject = PublishSubject.create<ChangeFilterEvent>()

  override fun buildModels(data: List<EditorialContent>, isSingleApp: Boolean,
                           reactionConfiguration: ReactionConfiguration,
                           comments: CommentsResponseModel) {
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

    add(getCommentsModels(comments))
  }

  fun getCommentsModels(comments: CommentsResponseModel): List<EpoxyModel<*>> {
    val models = ArrayList<EpoxyModel<*>>()

    models.add(
        CommentsTitleModel_()
            .id("comments_title")
            .title(commentsTitle)
            .commentFilters(comments.filters)
            .filterChangeSubject(filterChangedEventSubject)
            .count(comments.total)
    )
    for (comment in comments.comments) {
      models.add(
          CommentGroupModel(dateUtils, comment)
      )
    }
    if (comments.hasMore() || comments.loading) {
      models.add(LoadingViewModel_().id("progress"))
    }

    return models
  }
}