package cm.aptoide.pt.v8engine.timeline.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Time {
  @JsonProperty("seconds") private double elapsedTimeInSeconds;
  @JsonProperty("human") private String elapsedTimeInHumanSeconds;

  public double getElapsedTimeInSeconds() {
    return elapsedTimeInSeconds;
  }

  public void setElapsedTimeInSeconds(double elapsedTimeInSeconds) {
    this.elapsedTimeInSeconds = elapsedTimeInSeconds;
  }

  public String getElapsedTimeInHumanSeconds() {
    return elapsedTimeInHumanSeconds;
  }

  public void setElapsedTimeInHumanSeconds(String elapsedTimeInHumanSeconds) {
    this.elapsedTimeInHumanSeconds = elapsedTimeInHumanSeconds;
  }
}
