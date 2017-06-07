package cm.aptoide.pt.v8engine.install.installer;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.root.exceptions.RootDeniedException;
import cm.aptoide.pt.root.execution.Command;
import cm.aptoide.pt.root.execution.Shell;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
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
  private static final String TAG = RootCommandOnSubscribe.class.getSimpleName();
  private final int installId;
  private boolean success;
  private String command;

  public RootCommandOnSubscribe(int installId, String command) {
    this.installId = installId;
    this.command = command;
    success = false;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    Logger.d(TAG, "call: start");
    try {
      Shell shell = RootShell.getShell(true);

      if (!RootShell.isRootAvailable()) {
        subscriber.onError(new InstallationException("No root permissions"));
        Logger.d(TAG, "call: root available");
        return;
      }
      if (!RootShell.isAccessGiven()) {
        subscriber.onError(new InstallationException("User didn't accept root permissions"));
        Logger.d(TAG, "call: access given");
        return;
      }

      Command installCommand = new Command(installId, command) {
        @Override public void commandOutput(int id, String line) {
          Logger.d(TAG, "commandOutput: " + line);
          if (id == installId && line.toLowerCase().equals(SUCCESS_OUTPUT_CONFIRMATION)) {
            success = true;
          }
          super.commandOutput(id, line);
        }

        @Override public void commandTerminated(int id, String reason) {
          Logger.d(TAG, "commandTerminated: " + reason);
          super.commandTerminated(id, reason);
          if (!subscriber.isUnsubscribed() && installId == id) {
            subscriber.onError(new IllegalStateException(reason));
          }
        }

        @Override public void commandCompleted(int id, int exitcode) {
          Logger.d(TAG, "commandCompleted: " + exitcode);
          if (!subscriber.isUnsubscribed() && installId == id) {
            if (success || exitcode == 0) {
              subscriber.onCompleted();
            } else {
              subscriber.onError(new IllegalStateException("success message wasn't received"));
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
      subscriber.onError(e);
    }
  }
}

