package cm.aptoide.pt.downloadmanager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.Semaphore;

public class CustomHandler extends Handler {

	public CustomHandler(String name) {
		this(name, Process.THREAD_PRIORITY_BACKGROUND);
	}

	protected CustomHandler(String handlerName, int handlerPriority) {
		super(startHandlerThread(handlerName, handlerPriority));
	}

	private static Looper startHandlerThread(String name, int priority) {
		final Semaphore semaphore = new Semaphore(0);
		HandlerThread handlerThread = new HandlerThread(name, priority) {
			protected void onLooperPrepared() {
				semaphore.release();
			}
		};
		handlerThread.start();
		semaphore.acquireUninterruptibly();
		return handlerThread.getLooper();
	}

	public Void quit() {
		getLooper().quit();
		return null;
	}
}