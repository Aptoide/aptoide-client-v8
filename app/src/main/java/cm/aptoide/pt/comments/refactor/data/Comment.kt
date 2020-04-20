package cm.aptoide.pt.comments.refactor.data

import java.util.*
import kotlin.collections.ArrayList

data class Comment(val id: Long = 1, val message: String = "", val user: User? = null,
                   private val replies: ArrayList<Comment> = ArrayList(),
                   val repliesNr: Int = -1, val repliesToShowNr: Int = 0,
                   val loadingReplies: Boolean, val date: Date? = null) {

  constructor(comment: Comment, repliesToShowNr: Int) : this(comment.id, comment.message,
      comment.user, comment.replies, comment.repliesNr, repliesToShowNr, comment.loadingReplies,
      comment.date)

  constructor(comment: Comment, repliesToShowNr: Int, loadingReplies: Boolean) : this(comment.id,
      comment.message,
      comment.user, comment.replies, comment.repliesNr, repliesToShowNr, loadingReplies,
      comment.date)

  constructor(comment: Comment, loadingReplies: Boolean) : this(comment.id, comment.message,
      comment.user, comment.replies, comment.repliesNr, comment.repliesToShowNr, loadingReplies,
      comment.date)

  constructor(comment: Comment, replies: ArrayList<Comment>, loadingReplies: Boolean) : this(
      comment.id, comment.message,
      comment.user, replies, comment.repliesNr, comment.repliesToShowNr, loadingReplies,
      comment.date)


  fun setReplies(replyList: List<Comment>) {
    replies.clear()
    replies.addAll(replyList)
  }

  fun getReplies(): List<Comment> {
    return replies
  }
}