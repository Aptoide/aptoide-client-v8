package cm.aptoide.pt.app;

import java.util.Date;

/**
 * Created by D01 on 22/05/2018.
 */

public class AppReview {
  private final long id;
  private final String title;
  private final String body;
  private final Date added;
  private final Date modified;
  private final ReviewStats reviewStats;
  private final ReviewComment reviewComment;
  private final ReviewUser reviewUser;

  public AppReview(long id, String title, String body, Date added, Date modified,
      ReviewStats reviewStats, ReviewComment reviewComment, ReviewUser reviewUser) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.added = added;
    this.modified = modified;
    this.reviewStats = reviewStats;
    this.reviewComment = reviewComment;
    this.reviewUser = reviewUser;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public Date getAdded() {
    return added;
  }

  public Date getModified() {
    return modified;
  }

  public ReviewStats getReviewStats() {
    return reviewStats;
  }

  public ReviewComment getReviewComment() {
    return reviewComment;
  }

  public ReviewUser getReviewUser() {
    return reviewUser;
  }
}
