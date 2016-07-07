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

	public BaseBodyWithStore(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, Long storeId) {
		super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q);
		this.storeId = storeId;
		this.storeUser = storeUser;
		this.storePassSha1 = storePassSha1;
	}

	public BaseBodyWithStore(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, String storeName) {
		super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q);
		this.storeName = storeName;
		this.storeUser = storeUser;
		this.storePassSha1 = storePassSha1;
	}
}
