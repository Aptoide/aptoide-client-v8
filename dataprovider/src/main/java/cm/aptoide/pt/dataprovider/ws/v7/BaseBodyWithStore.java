/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 23-05-2016.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseBodyWithStore extends BaseBody {

	private Long storeId;
	private String storeName;
	private String storeUser;
	private String storePassSha1;
}
