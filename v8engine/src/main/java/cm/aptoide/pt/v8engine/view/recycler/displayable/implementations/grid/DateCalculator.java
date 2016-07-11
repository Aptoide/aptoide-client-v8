package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;

import java.util.Date;

import cm.aptoide.pt.utils.AptoideUtils;

public class DateCalculator {

	public String getTimeSinceDate(Context context, Date date) {
		return AptoideUtils.DateTimeU.getInstance(context).getTimeDiffAll(context, date.getTime());
	}
}