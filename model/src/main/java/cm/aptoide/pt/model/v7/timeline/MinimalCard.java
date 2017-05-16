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
  private UserSharerTimeline owner;
  private List<UserSharerTimeline> sharers;

  public MinimalCard(@JsonProperty("card_id") String cardId,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("owner") UserSharerTimeline owner,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.cardId = cardId;
    this.date = date;
    this.owner = owner;
    this.sharers = sharers;
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
}
