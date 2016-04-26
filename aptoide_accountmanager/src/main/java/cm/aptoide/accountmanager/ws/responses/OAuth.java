/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import java.util.List;

import lombok.Data;

/**
 * Created by rmateus on 01-07-2014.
 */
@Data
public class OAuth {

	private String accessToken;
	private String refresh_token;
	private String error_description;
	private List<ErrorResponse> errors;
	private String status;
}
