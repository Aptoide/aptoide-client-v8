/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base body that every request should use. If more information should be provided this class should be extended.
 */
@Data
@EqualsAndHashCode
public class BaseBody {

	@JsonProperty("aptoide_uid") protected String aptoideId;
	protected String accessToken;
	protected int aptoideVercode;
	protected String cdn;
	protected String lang;
	protected boolean mature;
	protected String q;
}
