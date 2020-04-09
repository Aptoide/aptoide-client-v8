package cm.aptoide.pt.comments.refactor.data

import java.util.*

data class CommentsResponseModel(val comments: List<Comment>, val offset: Int,
                                 val loading: Boolean) {
  constructor(loading: Boolean) : this(Collections.emptyList(), -1, loading)
  constructor(comments: List<Comment>, offset: Int) : this(comments, offset, false)

}