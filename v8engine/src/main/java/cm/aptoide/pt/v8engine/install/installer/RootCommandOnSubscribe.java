package cm.aptoide.pt.v8engine.install.installer;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.root.exceptions.RootDeniedException;
import cm.aptoide.pt.root.execution.Command;
import cm.aptoide.pt.root.execution.Shell;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by trinkes on 03/04/2017.
 */

public class RootCommandOnSubscribe implements Observable.OnSubscribe<Void> {
  private static final String TAG = RootCommandOnSubscribe.class.getSimpleName();
  public static int INSTALL_ID = 10;
  private final File file;

  public RootCommandOnSubscribe(File file) {
    this.file = file;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    try {
      Shell shell = RootShell.getShell(true);

      if (!RootShell.isRootAvailable()) {
        throw new InstallationException("No root permissions");
      }
      if (!RootShell.isAccessGiven()) {
        subscriber.onError(new InstallationException("User doesn't allow root installation"));
      }

      Command installCommand = new Command(INSTALL_ID, 120000, "pm install -r " + file.getPath()) {
        @Override public void commandOutput(int id, String line) {
          Logger.d(TAG, "commandOutput: " + line);
          super.commandOutput(id, line);
        }

        @Override public void commandTerminated(int id, String reason) {
          Logger.d(TAG, "commandTerminated: " + reason);
          super.commandTerminated(id, reason);
          if (!subscriber.isUnsubscribed() && INSTALL_ID == id) {
            subscriber.onError(new IllegalStateException(reason));
          }
        }

        @Override public void commandCompleted(int id, int exitcode) {
          Logger.d(TAG, "commandCompleted: " + exitcode);
          if (!subscriber.isUnsubscribed() && INSTALL_ID == id) {
            subscriber.onCompleted();
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

