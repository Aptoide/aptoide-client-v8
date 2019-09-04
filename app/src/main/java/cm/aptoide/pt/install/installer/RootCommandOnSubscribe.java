package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.RootCommandTimeoutException;
import cm.aptoide.pt.install.exception.InstallationException;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.root.exceptions.RootDeniedException;
import cm.aptoide.pt.root.execution.Command;
import cm.aptoide.pt.root.execution.Shell;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by trinkes on 03/04/2017.
 */

public class RootCommandOnSubscribe implements Observable.OnSubscribe<Void> {
  public static final String SUCCESS_OUTPUT_CONFIRMATION = "success"; // lower case
  public static final String TIMEOUT_EXCEPTION = "Timeout Exception";
  private static final String TAG = RootCommandOnSubscribe.class.getSimpleName();
  private final int installId;
  private boolean success;
  private String command;
  private InstallerAnalytics analytics;

  public RootCommandOnSubscribe(int installId, String command, InstallerAnalytics analytics) {
    this.installId = installId;
    this.command = command;
    this.analytics = analytics;
    success = false;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    Logger.getInstance()
        .d(TAG, "call: start with installation id: " + installId);
    try {
      Shell shell = RootShell.getShell(true);

      if (!RootShell.isRootAvailable()) {
        subscriber.onError(new InstallationException("No root permissions"));
        Logger.getInstance()
            .d(TAG, "call: root not available");
        return;
      }
      analytics.rootInstallStart();
      Command installCommand = new Command(installId, command) {
        @Override public void commandOutput(int id, String line) {
          Logger.getInstance()
              .d(TAG, "commandOutput: " + line);
          if (id == installId && line.toLowerCase()
              .equals(SUCCESS_OUTPUT_CONFIRMATION)) {
            success = true;
          }
          super.commandOutput(id, line);
        }

        @Override public void commandTerminated(int id, String reason) {
          Logger.getInstance()
              .d(TAG, "commandTerminated: " + reason);
          super.commandTerminated(id, reason);
          if (installId == id) {
            if (reason.equals(TIMEOUT_EXCEPTION)) {
              analytics.rootInstallTimeout();
              subscriber.onError(new RootCommandTimeoutException());
            } else if (!subscriber.isUnsubscribed()) {
              IllegalStateException e = new IllegalStateException(reason);
              analytics.rootInstallFail(e);
              subscriber.onError(e);
            }
          }
        }

        @Override public void commandCompleted(int id, int exitcode) {
          Logger.getInstance()
              .d(TAG, "commandCompleted: " + exitcode);
          if (!subscriber.isUnsubscribed() && installId == id) {
            if (success || exitcode == 0) {
              subscriber.onCompleted();
              analytics.rootInstallCompleted(exitcode);
            } else {
              IllegalStateException e = new IllegalStateException(
                  "success message wasn't received. Exit code: " + exitcode);
              analytics.rootInstallFail(e);
              subscriber.onError(e);
            }
          }
          super.commandCompleted(id, exitcode);
        }
      };

      subscriber.add(Subscriptions.create(() -> {
        try {
          shell.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }));
      shell.add(installCommand);
    } catch (IOException | TimeoutException | RootDeniedException e) {
      if (e instanceof RootDeniedException) {
        analytics.rootInstallCancelled();
        subscriber.onError(new InstallationException("User didn't accept root permissions"));
      } else if (e instanceof TimeoutException) {
        ((TimeoutException) e).printStackTrace();
        subscriber.onError(new RootCommandTimeoutException());
        analytics.rootInstallTimeout();
        Logger.getInstance()
            .d(TAG, "call: timeout reached");
      } else {
        analytics.rootInstallFail(e);
        subscriber.onError(e);
      }
    }
  }
}