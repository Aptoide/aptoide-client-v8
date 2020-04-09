package cm.aptoide.pt.comments.refactor.data

import java.util.*
import kotlin.collections.ArrayList

data class Comment(val id: Long = 1, val message: String = "", val user: User? = null,
                   private val replies: ArrayList<Comment> = ArrayList(),
                   val repliesNr: Int = -1,
                   val date: Date? = null) {

  fun setReplies(replyList: List<Comment>) {
    replies.clear()
    replies.addAll(replyList)
  }

  fun getReplies(): List<Comment> {
    return replies
  }
}