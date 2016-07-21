/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2;

import java.util.List;

/**
 * Created by j-pac on 30-05-2014.
 */
public class GenericResponseV2 {

	String status;

	List<ErrorResponse> errors;

	public String getStatus() {
		return status;
	}

	public List<ErrorResponse> getErrors() {
		return errors;
	}
}
