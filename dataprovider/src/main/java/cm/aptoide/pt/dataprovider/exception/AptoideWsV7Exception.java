/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.exception;

import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 19-05-2016.
 */
@Data
@Accessors(chain = true)
public class AptoideWsV7Exception extends Throwable {

	private BaseV7Response baseResponse;

	public AptoideWsV7Exception(Throwable cause) {
		super(cause);
	}
}
