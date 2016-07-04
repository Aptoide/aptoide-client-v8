/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base body that every request should use. If more information should be provided this class should be extended.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class BaseBody {

	@Getter @JsonProperty("aptoide_uid") private String aptoideId;
	@Setter @Getter @JsonProperty("access_token") private String accessToken;
	@Getter @JsonProperty("aptoide_vercode") private int aptoideVercode;
	@Getter private String cdn;

	protected static int getDefaultLimit() {
		return 10;
	}
}
