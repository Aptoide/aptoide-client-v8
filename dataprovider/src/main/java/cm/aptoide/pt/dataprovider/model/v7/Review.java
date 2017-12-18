/*
 * Copyright (c) 2016.
 * Modified on 09/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * Created on 20/07/16.
 */
public class Review {

  private long id;
  private String title;
  private String body;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;
  private User user;
  private Stats stats;
  private Comments comments;
  private ListComments commentList;

  public Review() {
  }

  public boolean hasComments() {
    return commentList != null
        && commentList.getDataList() != null
        && commentList.getDataList()
        .getList() != null
        && !commentList.getDataList()
        .getList()
        .isEmpty();
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return this.body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Date getAdded() {
    return this.added;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public Date getModified() {
    return this.modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Stats getStats() {
    return this.stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public Comments getComments() {
    return this.comments;
  }

  public void setComments(Comments comments) {
    this.comments = comments;
  }

  public ListComments getCommentList() {
    return this.commentList;
  }

  public void setCommentList(ListComments commentList) {
    this.commentList = commentList;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $id = this.getId();
    result = result * PRIME + (int) ($id >>> 32 ^ $id);
    final Object $title = this.getTitle();
    result = result * PRIME + ($title == null ? 43 : $title.hashCode());
    final Object $body = this.getBody();
    result = result * PRIME + ($body == null ? 43 : $body.hashCode());
    final Object $added = this.getAdded();
    result = result * PRIME + ($added == null ? 43 : $added.hashCode());
    final Object $modified = this.getModified();
    result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
    final Object $user = this.getUser();
    result = result * PRIME + ($user == null ? 43 : $user.hashCode());
    final Object $stats = this.getStats();
    result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
    final Object $comments = this.getComments();
    result = result * PRIME + ($comments == null ? 43 : $comments.hashCode());
    final Object $commentList = this.getCommentList();
    result = result * PRIME + ($commentList == null ? 43 : $commentList.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Review;
  }

  public static class User {

    private long id;
    private String name;
    private String avatar;

    public User() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAvatar() {
      return this.avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    protected boolean canEqual(Object other) {
      return other instanceof User;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof User)) return false;
      final User other = (User) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$avatar = this.getAvatar();
      final Object other$avatar = other.getAvatar();
      if (this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $avatar = this.getAvatar();
      result = result * PRIME + ($avatar == null ? 43 : $avatar.hashCode());
      return result;
    }

    public String toString() {
      return "Review.User(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", avatar="
          + this.getAvatar()
          + ")";
    }
  }

  public static class Stats {

    private float rating;
    private long points;
    private long likes;
    private long comments;

    public Stats() {
    }

    public float getRating() {
      return this.rating;
    }

    public void setRating(float rating) {
      this.rating = rating;
    }

    public long getPoints() {
      return this.points;
    }

    public void setPoints(long points) {
      this.points = points;
    }

    public long getLikes() {
      return this.likes;
    }

    public void setLikes(long likes) {
      this.likes = likes;
    }

    public long getComments() {
      return this.comments;
    }

    public void setComments(long comments) {
      this.comments = comments;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Stats;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Stats)) return false;
      final Stats other = (Stats) o;
      if (!other.canEqual((Object) this)) return false;
      if (Float.compare(this.getRating(), other.getRating()) != 0) return false;
      if (this.getPoints() != other.getPoints()) return false;
      if (this.getLikes() != other.getLikes()) return false;
      if (this.getComments() != other.getComments()) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + Float.floatToIntBits(this.getRating());
      final long $points = this.getPoints();
      result = result * PRIME + (int) ($points >>> 32 ^ $points);
      final long $likes = this.getLikes();
      result = result * PRIME + (int) ($likes >>> 32 ^ $likes);
      final long $comments = this.getComments();
      result = result * PRIME + (int) ($comments >>> 32 ^ $comments);
      return result;
    }

    public String toString() {
      return "Review.Stats(rating="
          + this.getRating()
          + ", points="
          + this.getPoints()
          + ", likes="
          + this.getLikes()
          + ", comments="
          + this.getComments()
          + ")";
    }
  }

  public static class Comments {

    private long total;
    private String view;

    public Comments() {
    }

    public long getTotal() {
      return this.total;
    }

    public void setTotal(long total) {
      this.total = total;
    }

    public String getView() {
      return this.view;
    }

    public void setView(String view) {
      this.view = view;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Comments;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Comments)) return false;
      final Comments other = (Comments) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getTotal() != other.getTotal()) return false;
      final Object this$view = this.getView();
      final Object other$view = other.getView();
      if (this$view == null ? other$view != null : !this$view.equals(other$view)) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $total = this.getTotal();
      result = result * PRIME + (int) ($total >>> 32 ^ $total);
      final Object $view = this.getView();
      result = result * PRIME + ($view == null ? 43 : $view.hashCode());
      return result;
    }

    public String toString() {
      return "Review.Comments(total=" + this.getTotal() + ", view=" + this.getView() + ")";
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Review)) return false;
    final Review other = (Review) o;
    if (!other.canEqual((Object) this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$title = this.getTitle();
    final Object other$title = other.getTitle();
    if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
    final Object this$body = this.getBody();
    final Object other$body = other.getBody();
    if (this$body == null ? other$body != null : !this$body.equals(other$body)) return false;
    final Object this$added = this.getAdded();
    final Object other$added = other.getAdded();
    if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
    final Object this$modified = this.getModified();
    final Object other$modified = other.getModified();
    if (this$modified == null ? other$modified != null : !this$modified.equals(other$modified)) {
      return false;
    }
    final Object this$user = this.getUser();
    final Object other$user = other.getUser();
    if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
    final Object this$stats = this.getStats();
    final Object other$stats = other.getStats();
    if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
    final Object this$comments = this.getComments();
    final Object other$comments = other.getComments();
    if (this$comments == null ? other$comments != null : !this$comments.equals(other$comments)) {
      return false;
    }
    final Object this$commentList = this.getCommentList();
    final Object other$commentList = other.getCommentList();
    if (this$commentList == null ? other$commentList != null
        : !this$commentList.equals(other$commentList)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "Review(id="
        + this.getId()
        + ", title="
        + this.getTitle()
        + ", body="
        + this.getBody()
        + ", added="
        + this.getAdded()
        + ", modified="
        + this.getModified()
        + ", user="
        + this.getUser()
        + ", stats="
        + this.getStats()
        + ", comments="
        + this.getComments()
        + ", commentList="
        + this.getCommentList()
        + ")";
  }
}
