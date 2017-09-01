package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Feedback class
 */

@Data public class Feedback {
  private String email;

  /**
   * Feedback constructor
   *
   * @param email defines the feedback email for the partner
   */
  public Feedback(String email) {
    this.email = email;
  }
}