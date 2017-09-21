package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class GameTimelineItem implements TimelineItem<TimelineCard> {

  private final Game game;

  @JsonCreator
  public GameTimelineItem(@JsonProperty("data") Game game) {
    this.game = game;
  }

  @Override
  public Ab getAb() {
    return this.game.getAb();
  }

  @Override
  public Game getData() {
    return game;
  }
}
