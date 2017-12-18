/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by neuro on 20-04-2016.
 */
public class BaseV7Response {

  private Info info;
  private List<Error> errors;

  public BaseV7Response() {
  }

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

  public Info getInfo() {
    return this.info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public List<Error> getErrors() {
    return this.errors;
  }

  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $info = this.getInfo();
    result = result * PRIME + ($info == null ? 43 : $info.hashCode());
    final Object $errors = this.getErrors();
    result = result * PRIME + ($errors == null ? 43 : $errors.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof BaseV7Response;
  }

  public enum Type {
    FACEBOOK_1, FACEBOOK_2, TWITCH_1, TWITCH_2, TWITTER_1, TWITTER_2, YOUTUBE_1, YOUTUBE_2
  }

  public static class Info {

    private Status status;
    private Time time;

    public Info() {
    }

    public Info(Status status, Time time) {
      this.status = status;
      this.time = time;
    }

    public Status getStatus() {
      return this.status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }

    public Time getTime() {
      return this.time;
    }

    public void setTime(Time time) {
      this.time = time;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Info;
    }

    public enum Status {
      OK, QUEUED, FAIL, Processing
    }

    public static class Time {

      private double seconds;
      private String human;

      public Time() {
      }

      public double getSeconds() {
        return this.seconds;
      }

      public void setSeconds(double seconds) {
        this.seconds = seconds;
      }

      public String getHuman() {
        return this.human;
      }

      public void setHuman(String human) {
        this.human = human;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Time;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Time)) return false;
        final Time other = (Time) o;
        if (!other.canEqual((Object) this)) return false;
        if (Double.compare(this.getSeconds(), other.getSeconds()) != 0) return false;
        final Object this$human = this.getHuman();
        final Object other$human = other.getHuman();
        if (this$human == null ? other$human != null : !this$human.equals(other$human)) {
          return false;
        }
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $seconds = Double.doubleToLongBits(this.getSeconds());
        result = result * PRIME + (int) ($seconds >>> 32 ^ $seconds);
        final Object $human = this.getHuman();
        result = result * PRIME + ($human == null ? 43 : $human.hashCode());
        return result;
      }

      public String toString() {
        return "BaseV7Response.Info.Time(seconds="
            + this.getSeconds()
            + ", human="
            + this.getHuman()
            + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Info)) return false;
      final Info other = (Info) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$status = this.getStatus();
      final Object other$status = other.getStatus();
      if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
        return false;
      }
      final Object this$time = this.getTime();
      final Object other$time = other.getTime();
      if (this$time == null ? other$time != null : !this$time.equals(other$time)) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $status = this.getStatus();
      result = result * PRIME + ($status == null ? 43 : $status.hashCode());
      final Object $time = this.getTime();
      result = result * PRIME + ($time == null ? 43 : $time.hashCode());
      return result;
    }

    public String toString() {
      return "BaseV7Response.Info(status=" + this.getStatus() + ", time=" + this.getTime() + ")";
    }
  }

  public static class Error {

    private String code;
    private String description;
    private Details details;

    public Error() {
    }

    public String getCode() {
      return this.code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public Details getDetails() {
      return this.details;
    }

    public void setDetails(Details details) {
      this.details = details;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Error;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Error)) return false;
      final Error other = (Error) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$code = this.getCode();
      final Object other$code = other.getCode();
      if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
      final Object this$description = this.getDescription();
      final Object other$description = other.getDescription();
      if (this$description == null ? other$description != null
          : !this$description.equals(other$description)) {
        return false;
      }
      final Object this$details = this.getDetails();
      final Object other$details = other.getDetails();
      if (this$details == null ? other$details != null : !this$details.equals(other$details)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $code = this.getCode();
      result = result * PRIME + ($code == null ? 43 : $code.hashCode());
      final Object $description = this.getDescription();
      result = result * PRIME + ($description == null ? 43 : $description.hashCode());
      final Object $details = this.getDetails();
      result = result * PRIME + ($details == null ? 43 : $details.hashCode());
      return result;
    }

    public String toString() {
      return "BaseV7Response.Error(code="
          + this.getCode()
          + ", description="
          + this.getDescription()
          + ", details="
          + this.getDetails()
          + ")";
    }
  }  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BaseV7Response)) return false;
    final BaseV7Response other = (BaseV7Response) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$info = this.getInfo();
    final Object other$info = other.getInfo();
    if (this$info == null ? other$info != null : !this$info.equals(other$info)) return false;
    final Object this$errors = this.getErrors();
    final Object other$errors = other.getErrors();
    if (this$errors == null ? other$errors != null : !this$errors.equals(other$errors)) {
      return false;
    }
    return true;
  }

  public static class Details {
    //Is only necessary for store/set requests and only appears with a STORE-9 error
    @JsonProperty("store_links") private List<StoreLinks> storeLinks;

    public Details() {
    }

    public List<StoreLinks> getStoreLinks() {
      return this.storeLinks;
    }

    public void setStoreLinks(List<StoreLinks> storeLinks) {
      this.storeLinks = storeLinks;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Details;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Details)) return false;
      final Details other = (Details) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$storeLinks = this.getStoreLinks();
      final Object other$storeLinks = other.getStoreLinks();
      if (this$storeLinks == null ? other$storeLinks != null
          : !this$storeLinks.equals(other$storeLinks)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $storeLinks = this.getStoreLinks();
      result = result * PRIME + ($storeLinks == null ? 43 : $storeLinks.hashCode());
      return result;
    }

    public String toString() {
      return "BaseV7Response.Details(storeLinks=" + this.getStoreLinks() + ")";
    }
  }

  public static class StoreLinks {
    private Type type;
    private String url;
    private String error;

    public StoreLinks() {
    }

    public Type getType() {
      return this.type;
    }

    public void setType(Type type) {
      this.type = type;
    }

    public String getUrl() {
      return this.url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getError() {
      return this.error;
    }

    public void setError(String error) {
      this.error = error;
    }

    protected boolean canEqual(Object other) {
      return other instanceof StoreLinks;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof StoreLinks)) return false;
      final StoreLinks other = (StoreLinks) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$type = this.getType();
      final Object other$type = other.getType();
      if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
      final Object this$url = this.getUrl();
      final Object other$url = other.getUrl();
      if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
      final Object this$error = this.getError();
      final Object other$error = other.getError();
      if (this$error == null ? other$error != null : !this$error.equals(other$error)) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $type = this.getType();
      result = result * PRIME + ($type == null ? 43 : $type.hashCode());
      final Object $url = this.getUrl();
      result = result * PRIME + ($url == null ? 43 : $url.hashCode());
      final Object $error = this.getError();
      result = result * PRIME + ($error == null ? 43 : $error.hashCode());
      return result;
    }

    public String toString() {
      return "BaseV7Response.StoreLinks(type="
          + this.getType()
          + ", url="
          + this.getUrl()
          + ", error="
          + this.getError()
          + ")";
    }
  }



  public String toString() {
    return "BaseV7Response(info=" + this.getInfo() + ", errors=" + this.getErrors() + ")";
  }
}
