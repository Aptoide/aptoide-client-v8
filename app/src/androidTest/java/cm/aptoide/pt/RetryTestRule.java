package cm.aptoide.pt;

/**
 * Created by jose_messejana on 10-10-2017.
 */

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link TestRule} that implements logic to try a test any number of times before giving up
 * and allowing it to fail.
 */
public class RetryTestRule implements TestRule {

  private static final String TAG = RetryTestRule.class.getSimpleName();

  private final int mRetryCount;

  public RetryTestRule(int retryCount) {
    mRetryCount = retryCount;
  }

  @Override public Statement apply(Statement base, Description description) {
    return new RetryStatement(base, description, mRetryCount);
  }

  private static class RetryStatement extends Statement {

    private final Statement mBase;
    private final Description mDescription;
    private final int mRetryCount;

    private RetryStatement(Statement base, Description description, int retryCount) {
      mBase = base;
      mDescription = description;
      mRetryCount = retryCount;
    }

    @Override public void evaluate() throws Throwable {
      Throwable testError = null;

      for (int i = 0; i < mRetryCount; i++) {
        try {
          mBase.evaluate();
          return;
        } catch (Throwable t) {
          testError = t;
        }
      }
      throw testError;
    }
  }
}