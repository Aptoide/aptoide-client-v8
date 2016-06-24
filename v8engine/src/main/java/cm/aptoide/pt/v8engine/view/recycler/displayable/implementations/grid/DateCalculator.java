package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import java.util.Date;

public class DateCalculator {

	public int getHoursSinceDate(Date date) {
		if (date != null) {
			long interval = new Date().getTime() - date.getTime();
			return (int) (interval / (1000 * 60 * 60));
		}
		return 0;
	}
}