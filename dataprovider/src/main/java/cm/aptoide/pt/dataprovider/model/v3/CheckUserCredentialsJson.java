/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by brutus on 09-12-2013.
 */
public class CheckUserCredentialsJson extends BaseV3Response {

  public int id;
  public String token;
  public String repo;
  public String avatar;
  public String username;
  public String email;
  public Settings settings;
  public String access;
  @JsonProperty("access_confirmed") public boolean accessConfirmed;
  public String ravatarHd;

  public CheckUserCredentialsJson() {
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRepo() {
    return this.repo;
  }

  public void setRepo(String repo) {
    this.repo = repo;
  }

  public String getAvatar() {
    return this.avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Settings getSettings() {
    return this.settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public String getAccess() {
    return this.access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  public boolean isAccessConfirmed() {
    return this.accessConfirmed;
  }

  public void setAccessConfirmed(boolean accessConfirmed) {
    this.accessConfirmed = accessConfirmed;
  }

  public String getRavatarHd() {
    return this.ravatarHd;
  }

  public void setRavatarHd(String ravatarHd) {
    this.ravatarHd = ravatarHd;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    result = result * PRIME + this.getId();
    final Object $token = this.getToken();
    result = result * PRIME + ($token == null ? 43 : $token.hashCode());
    final Object $repo = this.getRepo();
    result = result * PRIME + ($repo == null ? 43 : $repo.hashCode());
    final Object $avatar = this.getAvatar();
    result = result * PRIME + ($avatar == null ? 43 : $avatar.hashCode());
    final Object $username = this.getUsername();
    result = result * PRIME + ($username == null ? 43 : $username.hashCode());
    final Object $email = this.getEmail();
    result = result * PRIME + ($email == null ? 43 : $email.hashCode());
    final Object $settings = this.getSettings();
    result = result * PRIME + ($settings == null ? 43 : $settings.hashCode());
    final Object $access = this.getAccess();
    result = result * PRIME + ($access == null ? 43 : $access.hashCode());
    result = result * PRIME + (this.isAccessConfirmed() ? 79 : 97);
    final Object $ravatarHd = this.getRavatarHd();
    result = result * PRIME + ($ravatarHd == null ? 43 : $ravatarHd.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof CheckUserCredentialsJson)) return false;
    final CheckUserCredentialsJson other = (CheckUserCredentialsJson) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$token = this.getToken();
    final Object other$token = other.getToken();
    if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
    final Object this$repo = this.getRepo();
    final Object other$repo = other.getRepo();
    if (this$repo == null ? other$repo != null : !this$repo.equals(other$repo)) return false;
    final Object this$avatar = this.getAvatar();
    final Object other$avatar = other.getAvatar();
    if (this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar)) {
      return false;
    }
    final Object this$username = this.getUsername();
    final Object other$username = other.getUsername();
    if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
      return false;
    }
    final Object this$email = this.getEmail();
    final Object other$email = other.getEmail();
    if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
    final Object this$settings = this.getSettings();
    final Object other$settings = other.getSettings();
    if (this$settings == null ? other$settings != null : !this$settings.equals(other$settings)) {
      return false;
    }
    final Object this$access = this.getAccess();
    final Object other$access = other.getAccess();
    if (this$access == null ? other$access != null : !this$access.equals(other$access)) {
      return false;
    }
    if (this.isAccessConfirmed() != other.isAccessConfirmed()) return false;
    final Object this$ravatarHd = this.getRavatarHd();
    final Object other$ravatarHd = other.getRavatarHd();
    if (this$ravatarHd == null ? other$ravatarHd != null
        : !this$ravatarHd.equals(other$ravatarHd)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "CheckUserCredentialsJson(id="
        + this.getId()
        + ", token="
        + this.getToken()
        + ", repo="
        + this.getRepo()
        + ", avatar="
        + this.getAvatar()
        + ", username="
        + this.getUsername()
        + ", email="
        + this.getEmail()
        + ", settings="
        + this.getSettings()
        + ", access="
        + this.getAccess()
        + ", accessConfirmed="
        + this.isAccessConfirmed()
        + ", ravatarHd="
        + this.getRavatarHd()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof CheckUserCredentialsJson;
  }

  public static class Settings {
    @JsonProperty("matureswitch") public String matureswitch;

    public Settings() {
    }

    public String getMatureswitch() {
      return this.matureswitch;
    }

    public void setMatureswitch(String matureswitch) {
      this.matureswitch = matureswitch;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $matureswitch = this.getMatureswitch();
      result = result * PRIME + ($matureswitch == null ? 43 : $matureswitch.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Settings)) return false;
      final Settings other = (Settings) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$matureswitch = this.getMatureswitch();
      final Object other$matureswitch = other.getMatureswitch();
      if (this$matureswitch == null ? other$matureswitch != null
          : !this$matureswitch.equals(other$matureswitch)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "CheckUserCredentialsJson.Settings(matureswitch=" + this.getMatureswitch() + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Settings;
    }
  }
}
