package cm.aptoide.pt.editorial.epoxy.comments

import cm.aptoide.pt.comments.refactor.data.Comment

data class CommentEvent(val comment: Comment, val type: Type) {

  enum class Type {
    USER_PROFILE_CLICK, REPLY_CLICK, SHOW_REPLIES_CLICK
  }
}