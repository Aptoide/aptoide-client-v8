package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel

interface CommentsViewI {
  fun setTitle(titleString: String)
  fun populateComments(response: CommentsResponseModel)
}