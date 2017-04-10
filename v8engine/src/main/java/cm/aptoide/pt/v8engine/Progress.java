/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

/**
 * Created by marcelobenites on 9/30/16.
 */

public class Progress<T> {

  /**
   * first state and when download is paused
   */
  public static final int INACTIVE = 0;
  /**
   * when installation is done
   */
  public static final int DONE = 1;
  /**
   * download is running or install manager is installing
   */
  public static final int ACTIVE = 2;
  /**
   * when an error occurs
   */
  public static final int ERROR = 4;

  private final T request;
  private final int max;

  private boolean indeterminate;
  private int current;
  private int speed;
  private int state;
  private int installationType;

  public Progress(T request, boolean indeterminate, int max, int initialProgress, int initialSpeed,
      int state, int installationType) {
    this.request = request;
    this.indeterminate = indeterminate;
    this.max = max;
    this.current = initialProgress;
    this.speed = initialSpeed;
    this.state = state;
    this.installationType = installationType;
  }

  public int getInstallationType() {
    return installationType;
  }

  public T getRequest() {
    return request;
  }

  public boolean isIndeterminate() {
    return indeterminate;
  }

  public void setIndeterminate(boolean indeterminate) {
    this.indeterminate = indeterminate;
  }

  public int getMax() {
    return max;
  }

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }
}