package cm.aptoide.pt.comments.refactor.data

import java.util.*

data class CommentsResponseModel(val comments: List<Comment>, val offset: Int,
                                 val total: Int,
                                 val loading: Boolean) {
  constructor(loading: Boolean) : this(Collections.emptyList(), -1, -1, loading)
  constructor(comments: List<Comment>, offset: Int, total: Int) : this(comments, offset, total,
      false)

  fun hasMore(): Boolean {
    return total > offset
  }
}