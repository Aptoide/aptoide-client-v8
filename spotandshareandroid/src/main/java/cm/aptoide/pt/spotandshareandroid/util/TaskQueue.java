package cm.aptoide.pt.spotandshareandroid.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
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

  public <T> Single<T> submitTask(Single<T> single) {
    return single.compose(single1 -> single1.subscribeOn(Schedulers.from(executorService)));
  }

  public Completable submitTask(Completable completable) {
    return submitTask(completable.toSingle(() -> null)).toCompletable();
  }

  public Completable submitTask(Action0 action0) {
    return submitTask(Completable.fromAction(action0));
  }

  public void shutdown() {
    executorService.shutdown();
  }

  public void shutdownNow() {
    executorService.shutdownNow();
  }
}
