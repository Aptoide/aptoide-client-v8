/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

/**
 * Created by marcelobenites on 9/30/16.
 */

public class Progress<T> {

  private final T request;
  private final int total;

  private boolean indeterminate;
  private int currentProgress;
  private int speed;
  private boolean done;

  public Progress(T request, boolean indeterminate, int total, int initialProgress,
      int initialSpeed, boolean done) {
    this.request = request;
    this.indeterminate = indeterminate;
    this.total = total;
    this.currentProgress = initialProgress;
    this.speed = initialSpeed;
    this.done = done;
  }

  public T getRequest() {
    return request;
  }

  public boolean isIndeterminate() {
    return indeterminate;
  }

  public int getTotal() {
    return total;
  }

  public int getCurrentProgress() {
    return currentProgress;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public void setIndeterminate(boolean indeterminate) {
    this.indeterminate = indeterminate;
  }

  public void setCurrentProgress(int currentProgress) {
    this.currentProgress = currentProgress;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public boolean isDone() {
    return done;
  }
}