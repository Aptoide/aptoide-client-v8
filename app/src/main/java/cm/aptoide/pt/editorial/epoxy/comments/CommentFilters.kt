package cm.aptoide.pt.editorial.epoxy.comments

import cm.aptoide.pt.reviews.LanguageFilterHelper

data class CommentFilters(val filters: List<LanguageFilterHelper.LanguageFilter>,
                          val activePosition: Int) {

  fun getActiveFilter(): LanguageFilterHelper.LanguageFilter {
    return filters[activePosition]
  }
}