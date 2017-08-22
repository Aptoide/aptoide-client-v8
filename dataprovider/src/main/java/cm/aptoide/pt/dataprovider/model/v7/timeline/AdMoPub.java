package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class AdMoPub implements TimelineCard {

  private final String cardId;
  private final Date date;

  @JsonCreator public AdMoPub(@JsonProperty("uid") String cardId,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date) {
    this.cardId = cardId;
    this.date = date;
  }

  @Override public String getCardId() {
    return cardId;
  }

  @Override public Urls getUrls() {
    return null;
  }

  public Date getDate() {
    return date;
  }
}
