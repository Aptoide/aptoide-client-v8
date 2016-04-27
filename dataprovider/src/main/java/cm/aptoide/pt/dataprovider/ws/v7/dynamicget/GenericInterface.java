/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.dynamicget;

import cm.aptoide.pt.dataprovider.ws.v7.V7;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GenericInterface extends V7<Void> {

	private GenericInterface() {
	}

	public static Interfaces newInstance() {
		return new GenericInterface().createService();
	}

	@Override
	protected Observable<Void> loadDataFromNetwork(V7.Interfaces interfaces) {
		throw new RuntimeException("GenericInterface shouldn't call loadDataFromNetwork!");
	}
}
