/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Base body that every request should use. If more information should be provided this class should be extended.
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseBody {

	@JsonProperty("aptoide_uid") private String aptoideId;
	@JsonProperty("access_token") private String accessToken;
	@JsonProperty("aptoide_vercode") private int aptoideVercode;
	private String cdn;

	public BaseBody(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
		this.aptoideId = aptoideId;
		this.accessToken = accessToken;
		this.aptoideVercode = aptoideVercode;
		this.cdn = cdn;
	}
}
