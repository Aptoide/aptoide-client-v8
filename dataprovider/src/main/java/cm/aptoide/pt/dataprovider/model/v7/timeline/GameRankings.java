package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class GameRankings {

  @Getter private final int score;
  @Getter private final int cardsLeft;
  @Getter private final int gRanking;
  @Getter private final int lRanking;
  @Getter private final int fRanking;

  @JsonCreator
  public GameRankings(@JsonProperty("score") int score, @JsonProperty("cardsLeft") int cardsLeft,
      @JsonProperty("global") int gRanking, @JsonProperty("country") int lRanking,
      @JsonProperty("friends") int fRanking) {
    this.score = score;
    this.cardsLeft = cardsLeft;
    this.gRanking = gRanking;
    this.lRanking = lRanking;
    this.fRanking = fRanking;
  }

}

