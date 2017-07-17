/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 20-04-2016.
 */
@Data public class BaseV7Response {

  private Info info;
  private List<Error> errors;

  public Error getError() {
    if (errors != null && errors.size() > 0) {
      return errors.get(0);
    } else {
      return null;
    }
  }

  public boolean isOk() {
    return info != null && info.getStatus() == Info.Status.OK;
  }

  @Data public static class Info {

    private Status status;
    private Time time;

    public Info() {
    }

    public Info(Status status, Time time) {
      this.status = status;
      this.time = time;
    }

    public enum Status {
      OK, QUEUED, FAIL, PROCESSING
    }

    @Data public static class Time {

      private double seconds;
      private String human;
    }
  }

  @Data public static class Error {

    private String code;
    private String description;
  }
}
