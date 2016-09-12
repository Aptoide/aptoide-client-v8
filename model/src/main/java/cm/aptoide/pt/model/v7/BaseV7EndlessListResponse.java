/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import lombok.Data;

/**
 * Created by sithengineer on 18/08/16.
 */
@Data
public class BaseV7EndlessListResponse<T> extends BaseV7EndlessResponse {

	private List<T> list;

	@Override
	public int getTotal() {
		return list != null ? list.size() : 0;
	}

	@Override
	public int getNextSize() {
		return list != null ? NEXT_STEP : 0;
	}

	@Override
	public boolean hasData() {
		return list != null;
	}
}
