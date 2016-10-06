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
  private final int max;

  private boolean indeterminate;
  private int current;
  private int speed;
  private boolean done;

  public Progress(T request, boolean indeterminate, int max, int initialProgress,
      int initialSpeed, boolean done) {
    this.request = request;
    this.indeterminate = indeterminate;
    this.max = max;
    this.current = initialProgress;
    this.speed = initialSpeed;
    this.done = done;
  }

  public T getRequest() {
    return request;
  }

  public boolean isIndeterminate() {
    return indeterminate;
  }

  public int getMax() {
    return max;
  }

  public int getCurrent() {
    return current;
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

  public void setCurrent(int current) {
    this.current = current;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public boolean isDone() {
    return done;
  }
}