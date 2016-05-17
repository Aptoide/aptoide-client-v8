/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/05/2016.
 */

package cm.aptoide.pt.database.convert;

/**
 * Created by sithengineer on 17/05/16.
 */
public abstract class BaseConvert<F, T> {
	public abstract T convert(F f);
}
