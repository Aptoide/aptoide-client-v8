/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

/**
 * Created by marcelobenites on 9/30/16.
 */

public class Progress<T> {

  public static final int INACTIVE = 0;
  public static final int DONE = 1;
  public static final int ACTIVE = 2;
  public static final int ERROR = 4;
  private final T request;
  private final int max;

  private boolean indeterminate;
  private int current;
  private int speed;
  private int state;

  public Progress(T request, boolean indeterminate, int max, int initialProgress, int initialSpeed,
      int state) {
    this.request = request;
    this.indeterminate = indeterminate;
    this.max = max;
    this.current = initialProgress;
    this.speed = initialSpeed;
    this.state = state;
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

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }
}