/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 23-05-2016.
 */
@EqualsAndHashCode(callSuper = true)
public class BaseBodyWithStore extends BaseBody {

	@Getter private Long storeId;
	@Getter private String storeName;
	@Getter @Setter private String storeUser;
	@Getter @Setter private String storePassSha1;

	public BaseBodyWithStore(Long storeId) {
		this.storeId = storeId;
		this.storeUser = storeUser;
		this.storePassSha1 = storePassSha1;
	}

	public BaseBodyWithStore(String storeName) {
		this.storeName = storeName;
		this.storeUser = storeUser;
		this.storePassSha1 = storePassSha1;
	}
}
