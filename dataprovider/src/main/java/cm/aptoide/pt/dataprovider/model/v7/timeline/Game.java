package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Getter;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class Game implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final App rightAnswer;
  @Getter private final GameQuestion question;
  @Getter private final WrongAnswer wrongAnswer;
  @Getter private final GameRankings rankings;
  @Getter private final Ab ab;
  @Getter @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date timestamp;



  @JsonCreator
  public Game(@JsonProperty("uid") String cardId, @JsonProperty("question") GameQuestion question,
      @JsonProperty("timestamp") Date timestamp, @JsonProperty("app") App rightAnswer,
      @JsonProperty("wrongAnswer") WrongAnswer wrongAnswer,
      @JsonProperty("stats") GameRankings rankings, @JsonProperty("ab") Ab ab) {
    this.ab = ab;
    this.cardId = cardId;
    this.timestamp = timestamp;
    this.rightAnswer = rightAnswer;
    this.wrongAnswer = wrongAnswer;
    this.question = question;
    this.rankings = rankings;
  }

  @Override public Urls getUrls() {
    return null;
  }
}
