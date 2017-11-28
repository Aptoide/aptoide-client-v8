package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import retrofit2.http.GET;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class GameRankings {

  @Getter private final int score;
  @Getter private final Rankings ranking;

  @JsonCreator
  public GameRankings(@JsonProperty("score") int score,
      @JsonProperty("ranking") Rankings ranking) {
    this.score = score;
    this.ranking = ranking;
  }

  public static class Rankings {
    @Getter private final int global;
    @Getter private final int local;
    @Getter private final int friends;

    @JsonCreator
    public Rankings(@JsonProperty("global") int global, @JsonProperty("country") int local,
        @JsonProperty("friends") int friends){
      this.global = global;
      this.local = local;
      this.friends = friends;
    }
  }

}

