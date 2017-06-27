/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base body that every request should use. If more information should be provided this class
 * should be extended.
 */
public class BaseBody {
  @JsonProperty("aptoide_uid") private String aptoideId;
  private String accessToken;
  private int aptoideVercode;
  private String aptoideMd5sum;
  private String aptoidePackage;
  private String cdn;
  private String lang;
  private boolean mature;
  private String q;
  private String country;

  public String getAptoideId() {
    return aptoideId;
  }

  public void setAptoideId(String aptoideId) {
    this.aptoideId = aptoideId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public int getAptoideVercode() {
    return aptoideVercode;
  }

  public void setAptoideVercode(int aptoideVercode) {
    this.aptoideVercode = aptoideVercode;
  }

  public String getAptoideMd5sum() {
    return aptoideMd5sum;
  }

  public void setAptoideMd5sum(String aptoideMd5sum) {
    this.aptoideMd5sum = aptoideMd5sum;
  }

  public String getAptoidePackage() {
    return aptoidePackage;
  }

  public void setAptoidePackage(String aptoidePackage) {
    this.aptoidePackage = aptoidePackage;
  }

  public String getCdn() {
    return cdn;
  }

  public void setCdn(String cdn) {
    this.cdn = cdn;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public boolean isMature() {
    return mature;
  }

  public void setMature(boolean mature) {
    this.mature = mature;
  }

  public String getQ() {
    return q;
  }

  public void setQ(String q) {
    this.q = q;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
