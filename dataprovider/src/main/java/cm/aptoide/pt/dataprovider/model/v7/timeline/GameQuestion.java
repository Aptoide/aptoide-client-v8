package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by franciscocalado on 11/28/17.
 */

public class GameQuestion {

  @Getter private final String type;
  @Getter private final String questionText;
  @Getter private final String questionIcon;

  @JsonCreator
  public GameQuestion(@JsonProperty("type") String type, @JsonProperty("text") String question,
      @JsonProperty("icon") String questionIcon){
    this.type = type;
    this.questionText = question;
    this.questionIcon = questionIcon;
  }

}
