package cm.aptoide.pt.spotandshareandroid.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 04-07-2017.
 */

public class TaskQueue {

  private final ExecutorService executorService;

  public TaskQueue() {
    this(Executors.newSingleThreadExecutor());
  }

  public TaskQueue(int n) {
    this(Executors.newFixedThreadPool(n));
  }

  public TaskQueue(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public <T> Single<T> submitTask(Single<T> observable) {
    return observable.compose(
        observable1 -> observable1.subscribeOn(Schedulers.from(executorService)));
  }

  public void submitTask(Runnable runnable) {
    submitTask(Single.fromCallable(() -> {
      runnable.run();
      return null;
    })).subscribe(o -> {
    }, Throwable::printStackTrace);
  }

  public <T> Single<T> submitTask(Callable<T> callable) {
    return submitTask(Single.fromCallable(callable));
  }

  public void shutdown() {
    executorService.shutdown();
  }

  public void shutdownNow() {
    executorService.shutdownNow();
  }
}
