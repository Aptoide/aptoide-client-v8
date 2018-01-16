package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by franciscocalado on 1/11/18.
 */

public class CommentDataWrapper {
  private final BaseV7Response response;
  private final Long longAsId;
  private final Long previousCommentId;
  private final String stringAsId;

  public CommentDataWrapper(BaseV7Response response, Long longAsId, Long previousCommentId,
      String stringAsId) {
    this.response = response;
    this.longAsId = longAsId;
    this.previousCommentId = previousCommentId;
    this.stringAsId = stringAsId;
  }

  public BaseV7Response getResponse() {
    return response;
  }

  public Long getLongAsId() {
    return longAsId;
  }

  public Long getPreviousCommentId() {
    return previousCommentId;
  }

  public String getStringAsId() {
    return stringAsId;
  }
}
