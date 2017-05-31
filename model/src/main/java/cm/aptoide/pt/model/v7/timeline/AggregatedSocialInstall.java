package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Getter;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstall implements TimelineCard {

  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;
  @Getter private final String cardId;
  @Getter private final App app;
  @Getter private final Ab ab;
  @Getter private final Date date;

  public AggregatedSocialInstall(@JsonProperty("uid") String cardId,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
    this.cardId = cardId;
    this.ab = ab;
    this.date = date;
    if (!apps.isEmpty()) {
      this.app = apps.get(0);
    } else {
      this.app = null;
    }
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }
}
