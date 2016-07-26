/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 26/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Date;

import cm.aptoide.pt.utils.AptoideUtils;

public class DateCalculator {

	@NonNull
	public String getTimeSinceDate(@NonNull Context context, @NonNull Date date) {
		return AptoideUtils.DateTimeU.getInstance(context).getTimeDiffAll(context, date.getTime());
	}
}
