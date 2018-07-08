/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base body that every request should use. If more information should be provided this class
 * should
 * be extended.
 */
@Data @EqualsAndHashCode public class BaseBody implements AccessTokenBody {

  @JsonProperty("aptoide_uid") private String aptoideId;
  private String accessToken;
  private int aptoideVercode;
  private String cdn;
  private String lang;
  private boolean mature;
  private String q;
  private String country;
  private String aptoideMd5sum;
  private String aptoidePackage;
  private String oem_id;
  private String config_type;
}
