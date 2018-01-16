package cm.aptoide.pt.social.commentslist;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.SetComment;
import cm.aptoide.pt.logger.Logger;
import java.util.Date;

/**
 * Created by franciscocalado on 1/11/18.
 */

public class CommentMapper {
  private final AptoideAccountManager accountManager;

  public CommentMapper(AptoideAccountManager manager) {
    accountManager = manager;
  }

  public Comment mapToComment(CommentDataWrapper data) {

    Comment comment = new Comment();
    Comment.User user = new Comment.User();
    Comment.Parent parent = new Comment.Parent();

    if (!TextUtils.isEmpty(accountManager.getAccount()
        .getAvatar())) {
      user.setAvatar(accountManager.getAccount()
          .getAvatar());
    } else {
      if (!TextUtils.isEmpty(accountManager.getAccount()
          .getStore()
          .getAvatar())) {
        user.setAvatar(accountManager.getAccount()
            .getStore()
            .getAvatar());
      }
    }
    user.setName(accountManager.getAccount()
        .getNickname());

    comment.setUser(user);
    if (data.getResponse() instanceof SetComment) {
      SetComment converted = (SetComment) data.getResponse();
      comment.setBody(converted.getData()
          .getBody());
      comment.setAdded(new Date());
      comment.setId(converted.getData()
          .getId());
      Long prev = data.getPreviousCommentId();
      if (prev != null) {
        parent.setId(data.getPreviousCommentId());
        comment.setParent(parent);
      }
    } else {
      Logger.e("NO_TEXT", "No text was received");
      return null;
    }

    return comment;
  }
}
