package cm.aptoide.pt.spotandshare.socket.interfaces;

/**
 * Created by neuro on 07-04-2017.
 */

public interface FileLifecycleProvider<T> {

  FileServerLifecycle<T> newFileServerLifecycle();

  FileClientLifecycle<T> newFileClientLifecycle();
}
