package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 11/05/2017.
 * Minimal representation of a card model.
 */

public class MinimalCard {

  private String cardId;
  private Date date;
  private SocialCardStats stats;
  private UserSharerTimeline owner;
  private List<UserSharerTimeline> sharers;
  private List<UserTimeline> usersLikes;
  private List<SocialCard.CardComment> comments;
  private My my;

  public MinimalCard(@JsonProperty("card_id") String cardId,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("owner") UserSharerTimeline owner,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("my") My my,
      @JsonProperty("likes") List<UserTimeline> usersLikes) {
    this.usersLikes = usersLikes;
    this.cardId = cardId;
    this.my = my;
    this.date = date;
    this.owner = owner;
    this.sharers = sharers;
    this.stats = stats;
  }

  public List<SocialCard.CardComment> getComments() {
    return comments;
  }

  public void setComments(List<SocialCard.CardComment> comments) {
    this.comments = comments;
  }

  public List<UserTimeline> getUsersLikes() {
    return usersLikes;
  }

  public void setUsersLikes(List<UserTimeline> usersLikes) {
    this.usersLikes = usersLikes;
  }

  public SocialCardStats getStats() {
    return stats;
  }

  public void setStats(SocialCardStats stats) {
    this.stats = stats;
  }

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public UserSharerTimeline getOwner() {
    return owner;
  }

  public void setOwner(UserSharerTimeline owner) {
    this.owner = owner;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  public void setSharers(List<UserSharerTimeline> sharers) {
    this.sharers = sharers;
  }

  public My getMy() {
    return my;
  }

  public void setMy(My my) {
    this.my = my;
  }
}
