package cm.aptoide.pt.spotandshare.socket.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by neuro on 14-02-2017.
 */

public abstract class ScheduledStopable {

  private final ScheduledExecutorService scheduledExecutorService =
      Executors.newSingleThreadScheduledExecutor();

  private final int timeout;
  private ScheduledFuture<?> schedule;

  public ScheduledStopable(int timeout) {
    this.timeout = timeout;
  }

  public void start() {
    reserTimeout();
  }

  public void reserTimeout() {
    if (schedule != null) {
      schedule.cancel(false);
    }
    schedule = scheduledExecutorService.schedule(this::stop, timeout, TimeUnit.MILLISECONDS);
  }

  protected abstract void stop();
}
