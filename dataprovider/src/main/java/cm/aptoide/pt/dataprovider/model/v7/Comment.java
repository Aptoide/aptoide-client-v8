/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * Created by neuro on 04-07-2016.
 */
public class Comment {

  private long id;
  private String body;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  private User user;
  private Long parentReview;
  private Parent parent;
  private Stats stats;

  public Comment() {
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
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

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getParentReview() {
    return this.parentReview;
  }

  public void setParentReview(Long parentReview) {
    this.parentReview = parentReview;
  }

  public Parent getParent() {
    return this.parent;
  }

  public void setParent(Parent parent) {
    this.parent = parent;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $id = this.getId();
    result = result * PRIME + (int) ($id >>> 32 ^ $id);
    final Object $body = this.getBody();
    result = result * PRIME + ($body == null ? 43 : $body.hashCode());
    final Object $added = this.getAdded();
    result = result * PRIME + ($added == null ? 43 : $added.hashCode());
    final Object $user = this.getUser();
    result = result * PRIME + ($user == null ? 43 : $user.hashCode());
    final Object $parentReview = this.getParentReview();
    result = result * PRIME + ($parentReview == null ? 43 : $parentReview.hashCode());
    final Object $parent = this.getParent();
    result = result * PRIME + ($parent == null ? 43 : $parent.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Comment;
  }

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public enum Access {
    PUBLIC, PRIVATE, UNLISTED
  }

  public static class User {
    private long id;
    private String name;
    private String avatar;
    private Access access;

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

    public Access getAccess() {
      return this.access;
    }

    public void setAccess(Access access) {
      this.access = access;
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
      final Object this$access = this.getAccess();
      final Object other$access = other.getAccess();
      if (this$access == null ? other$access != null : !this$access.equals(other$access)) {
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
      final Object $access = this.getAccess();
      result = result * PRIME + ($access == null ? 43 : $access.hashCode());
      return result;
    }

    public String toString() {
      return "Comment.User(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", avatar="
          + this.getAvatar()
          + ", access="
          + this.getAccess()
          + ")";
    }
  }

  public static class Parent {
    private long id;

    public Parent() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Parent;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Parent)) return false;
      final Parent other = (Parent) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      return result;
    }

    public String toString() {
      return "Comment.Parent(id=" + this.getId() + ")";
    }
  }

  public static class Stats {
    private int points;
    private int comments;

    private Stats() {
    }

    public int getPoints() {
      return points;
    }

    public void setPoints(int points) {
      this.points = points;
    }

    public int getComments() {
      return comments;
    }

    public void setComments(int comments) {
      this.comments = comments;
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Comment)) return false;
    final Comment other = (Comment) o;
    if (!other.canEqual((Object) this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$body = this.getBody();
    final Object other$body = other.getBody();
    if (this$body == null ? other$body != null : !this$body.equals(other$body)) return false;
    final Object this$added = this.getAdded();
    final Object other$added = other.getAdded();
    if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
    final Object this$user = this.getUser();
    final Object other$user = other.getUser();
    if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
    final Object this$parentReview = this.getParentReview();
    final Object other$parentReview = other.getParentReview();
    if (this$parentReview == null ? other$parentReview != null
        : !this$parentReview.equals(other$parentReview)) {
      return false;
    }
    final Object this$parent = this.getParent();
    final Object other$parent = other.getParent();
    if (this$parent == null ? other$parent != null : !this$parent.equals(other$parent)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "Comment(id="
        + this.getId()
        + ", body="
        + this.getBody()
        + ", added="
        + this.getAdded()
        + ", user="
        + this.getUser()
        + ", parentReview="
        + this.getParentReview()
        + ", parent="
        + this.getParent()
        + ")";
  }
}
