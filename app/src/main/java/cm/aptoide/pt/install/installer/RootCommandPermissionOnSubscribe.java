package cm.aptoide.pt.install.installer;

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

public class RootCommandPermissionOnSubscribe implements Observable.OnSubscribe<Void> {

  public static final String SUCCESS_OUTPUT_CONFIRMATION = "success"; // lower case
  public static final String TIMEOUT_EXCEPTION = "Timeout Exception";
  private static final String TAG = "RootCommandPermissionOn";
  private String packageName;
  private boolean success;
  private String command;

  public RootCommandPermissionOnSubscribe(String packageName, String command) {
    this.packageName = packageName;
    this.command = command;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    int packageHashcode = packageName.hashCode();

    Logger.getInstance()
        .d(TAG, "call: called command: " + command);
    try {
      Shell shell = RootShell.getShell(true);

      if (!RootShell.isRootAvailable()) {
        subscriber.onError(new InstallationException("No root permissions"));
        Logger.getInstance()
            .d(TAG, "call: root not available");
        return;
      }

      Command installCommand = new Command(packageHashcode, command) {
        @Override public void commandOutput(int id, String line) {
          Logger.getInstance()
              .d(TAG, "commandOutput: " + line);
          if (id == packageHashcode && line.toLowerCase()
              .equals(SUCCESS_OUTPUT_CONFIRMATION)) {
            success = true;
          }
          super.commandOutput(id, line);
        }

        @Override public void commandTerminated(int id, String reason) {
          Logger.getInstance()
              .d(TAG, "commandTerminated: " + reason);
          super.commandTerminated(id, reason);
          if (packageHashcode == id) {
            if (reason.equals(TIMEOUT_EXCEPTION)) {
              subscriber.onError(new RootCommandTimeoutException());
            } else if (!subscriber.isUnsubscribed()) {
              IllegalStateException e = new IllegalStateException(reason);
              subscriber.onError(e);
            }
          }
        }

        @Override public void commandCompleted(int id, int exitcode) {
          Logger.getInstance()
              .d(TAG, "commandCompleted: " + exitcode);
          if (!subscriber.isUnsubscribed() && packageHashcode == id) {
            if (success || exitcode == 0) {
              subscriber.onCompleted();
            } else {
              IllegalStateException e = new IllegalStateException(
                  "success message wasn't received. Exit code: " + exitcode);
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
        subscriber.onError(new InstallationException("User didn't accept root permissions"));
      } else if (e instanceof TimeoutException) {
        subscriber.onError(new RootCommandTimeoutException());
        Logger.getInstance()
            .d(TAG, "call: timeout reached");
      } else {
        subscriber.onError(e);
      }
    }
  }
}
