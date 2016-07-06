/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import lombok.Data;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
public class DataList<T> {

	private int total;
	private int count;
	private int offset;
	private int limit;
	private int next;
	private int hidden;
	private boolean loaded;
	private List<T> list;
}
