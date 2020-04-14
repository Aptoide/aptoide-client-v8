package cm.aptoide.pt.comments.refactor.data

import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import java.util.*

data class CommentsResponseModel(val comments: List<Comment>, val offset: Int,
                                 val total: Int, val filters: CommentFilters,
                                 val loading: Boolean) {
  constructor(loading: Boolean, filters: CommentFilters) : this(Collections.emptyList(), -1, -1,
      filters,
      loading)

  constructor(comments: List<Comment>, offset: Int, total: Int, filters: CommentFilters) : this(
      comments,
      offset, total, filters,
      false)

  fun hasMore(): Boolean {
    return total > offset
  }
}