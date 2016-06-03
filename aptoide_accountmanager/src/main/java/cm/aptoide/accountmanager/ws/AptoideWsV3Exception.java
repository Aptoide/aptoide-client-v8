/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 19-05-2016.
 */
@Data
@Accessors(chain = true)
public class AptoideWsV3Exception extends Throwable {

	private GenericResponseV3 baseResponse;

	public AptoideWsV3Exception(Throwable cause) {
		super(cause);
	}
}
