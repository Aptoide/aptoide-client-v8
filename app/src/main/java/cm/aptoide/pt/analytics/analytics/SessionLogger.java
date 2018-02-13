package cm.aptoide.pt.analytics.analytics;

/**
 * Created by jose_messejana on 31-01-2018.
 */

public interface SessionLogger {

  /**
   * <p>Starts a session for an analytics platform.</p>
   */
  void startSession();

  /**
   * <p>Finishes an opened session for an analytics platform.</p>
   */
  void endSession();
}
